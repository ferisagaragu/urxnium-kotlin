package org.pechblenda.auth.service

import org.pechblenda.auth.entity.IUser
import org.pechblenda.auth.repository.IAuthRepository
import org.pechblenda.auth.service.`interface`.IAuthService
import org.pechblenda.auth.service.mail.AuthMail
import org.pechblenda.auth.service.message.AuthMessage
import org.pechblenda.exception.BadRequestException
import org.pechblenda.exception.UnauthenticatedException
import org.pechblenda.security.GoogleAuthentication
import org.pechblenda.security.JwtProvider
import org.pechblenda.security.OutlookAuthentication
import org.pechblenda.service.Request
import org.pechblenda.service.Response
import org.pechblenda.service.helper.Validation
import org.pechblenda.service.helper.ValidationType
import org.pechblenda.service.helper.Validations
import org.pechblenda.style.Avatar
import org.pechblenda.style.Color
import org.pechblenda.style.CategoryColor

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ClassPathResource
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import kotlin.reflect.KClass

import java.net.URLEncoder

import java.nio.charset.StandardCharsets
import java.util.*
import javax.servlet.http.HttpServletRequest
import org.pechblenda.auth.enum.AccountType
import kotlin.collections.LinkedHashMap

@Service
open class AuthService: IAuthService {

	@Autowired
	private lateinit var response: Response

	@Autowired
	private lateinit var passwordEncoder: PasswordEncoder

	@Autowired
	private lateinit var authenticationManager: AuthenticationManager

	@Autowired
	private lateinit var jwtProvider: JwtProvider

	@Autowired
	private lateinit var avatar: Avatar

	@Autowired
	private lateinit var authMail: AuthMail

	@Autowired
	private lateinit var authMessage: AuthMessage

	@Autowired
	private lateinit var googleAuthentication: GoogleAuthentication

	@Autowired
	private lateinit var outlookAuthentication: OutlookAuthentication

	private val authRepository: IAuthRepository<IUser, UUID>
	private val userEntity: KClass<*>
	private val propertiesMessage: Properties

	constructor(authRepository: Any, userEntity: KClass<*>) {
		this.authRepository = authRepository as IAuthRepository<IUser, UUID>
		this.userEntity = userEntity

		propertiesMessage = Properties()
		propertiesMessage.load(ClassPathResource("messages/auth-messages.properties").inputStream)
	}

	@Transactional(readOnly = true)
	override fun validateToken(authorization: String): ResponseEntity<Any> {
		val user = authRepository.findByUserName(
			SecurityContextHolder.getContext().authentication.name
		).orElseThrow {
			throw BadRequestException(authMessage.getUserNotFount())
		}

		if (!user.active) {
			throw BadRequestException(authMessage.getAccountNotActive())
		}

		if (!user.enabled) {
			throw BadRequestException(authMessage.getAccountBlocked())
		}

		val out = response.toMap(user)
		val provider = jwtProvider.decodeJwt(authorization.replace("Bearer ", ""))
		val session = mutableMapOf<String, Any>()

		session["token"] = authorization.replace("Bearer ", "")
		session["expiration"] = Date(provider.expiresAt.time - Date().time).time
		session["expirationDate"] = provider.expiresAt.toString()
		out["session"] = session

		return out
			.exclude(
				"password",
				"enabled",
				"active",
				"activatePassword",
				"refreshToken"
			)
			.ok()
	}

	@Transactional(readOnly = true)
	override fun canActivate(userUid: UUID): ResponseEntity<Any> {
		val user = authRepository.findById(userUid).orElseThrow {
			throw BadRequestException(authMessage.getActivateUserNotFount())
		}

		if (user.active) {
			throw BadRequestException(propertiesMessage.getProperty("message.auth.activate-user-invalid"))
		}

		val out = Request()
		out["canActivate"] = true

		return response.ok(out)
	}

	@Transactional(readOnly = true)
	override fun canChangePassword(activatePassword: UUID): ResponseEntity<Any> {
		if (!authRepository.existsByActivatePassword(activatePassword)) {
			throw BadRequestException(propertiesMessage.getProperty("message.auth.recover-code-invalid"))
		}

		val out = Request()
		out["canChangePassword"] = true

		return response.ok(out)
	}

	@Transactional(readOnly = true)
	override fun generateGoogleAuthenticationUrl(): ResponseEntity<Any> {
		val out = mutableMapOf<String, Any>()
		out["authUrl"] = googleAuthentication.generateAuthenticationUrl()
		return response.ok(out)
	}

	@Transactional(readOnly = true)
	override fun generateOutlookAuthenticationUrl(): ResponseEntity<Any> {
		val out = mutableMapOf<String, Any>()
		out["authUrl"] = outlookAuthentication.generateAuthenticationUrl()
		return response.ok(out)
	}

	@Transactional
	override fun activateAccount(request: Request): ResponseEntity<Any> {
		val user = request.to<IUser>(
			userEntity,
			Validations(
				Validation(
					"password",
					propertiesMessage.getProperty("message.auth.password-required"),
					ValidationType.NOT_NULL,
					ValidationType.NOT_BLANK
				)
			)
		)

		val findUser = authRepository.findById(user.uuid).orElseThrow {
			throw BadRequestException(propertiesMessage.getProperty("message.auth.user-not-fount"))
		}

		if (findUser.active) {
			throw BadRequestException(propertiesMessage.getProperty("message.auth.account-be-activated"))
		}

		findUser.password = passwordEncoder.encode(user.password)
		findUser.active = true

		return response.ok(propertiesMessage.getProperty("message.auth.account-activated"))
	}

	@Transactional
	override fun changePassword(request: Request): ResponseEntity<Any> {
		val user = request.to<IUser>(
			userEntity,
			Validations(
				Validation(
					"password",
					propertiesMessage.getProperty("message.auth.password-required"),
					ValidationType.NOT_NULL,
					ValidationType.NOT_BLANK
				)
			)
		)
		val userFind = authRepository.findByActivatePassword(user.activatePassword!!).orElseThrow {
			throw BadRequestException(propertiesMessage.getProperty("message.auth.account-not-match"))
		}

		if (!userFind.active) {
			throw BadRequestException(propertiesMessage.getProperty("message.auth.account-not-activate"))
		}

		userFind.activatePassword = null
		userFind.password = passwordEncoder.encode(user.password)

		return response.ok(propertiesMessage.getProperty("message.auth.password-changed"))
	}

	@Transactional
	override fun recoverPassword(request: Request): ResponseEntity<Any> {
		val user = request.to<IUser>(
			userEntity,
			Validations(
				Validation(
					"email",
					propertiesMessage.getProperty("message.auth.email-required"),
					ValidationType.NOT_NULL,
					ValidationType.NOT_BLANK
				)
			)
		)
		val userFind = authRepository.findByUserNameOrEmail(user.email).orElseThrow {
			BadRequestException(propertiesMessage.getProperty("message.auth.account-not-match"))
		}

		if (!userFind.active) {
			throw BadRequestException(propertiesMessage.getProperty("message.auth.account-not-activate"))
		}

		userFind.activatePassword = UUID.randomUUID()

		authMail.sendRecoverPasswordMail(userFind)

		return response.ok(
			propertiesMessage.getProperty("message.auth.recover-instruction").replace(
				"{email}",
				userFind.email
			)
		)
	}

	@Transactional
	override fun signUp(request: Request, servletRequest: HttpServletRequest): ResponseEntity<Any> {
		val user = request.to<IUser>(
			userEntity,
			Validations(
				Validation(
					"name",
					propertiesMessage.getProperty("message.auth.name-required"),
					ValidationType.NOT_NULL,
					ValidationType.NOT_BLANK
				),
				Validation(
					"surname",
					propertiesMessage.getProperty("message.auth.surname-required"),
					ValidationType.NOT_NULL,
					ValidationType.NOT_BLANK
				),
				Validation(
					"motherSurname",
					propertiesMessage.getProperty("message.auth.mother-surname-required"),
					ValidationType.NOT_NULL,
					ValidationType.NOT_BLANK
				),
				Validation(
					"userName",
					propertiesMessage.getProperty("message.auth.user-name-required"),
					ValidationType.NOT_NULL,
					ValidationType.NOT_BLANK
				),
				Validation(
					"email",
					propertiesMessage.getProperty("message.auth.email-required"),
					ValidationType.NOT_NULL,
					ValidationType.NOT_BLANK
				)
			)
		)
		val temporalPassword = UUID.randomUUID().toString()
		val color = Color().getMaterialColor(CategoryColor.MATERIAL_500)

		if (authRepository.existsByUserName(user.userName)) {
			throw BadRequestException(propertiesMessage.getProperty("message.auth.user-name-registered"))
		}

		if (authRepository.existsByEmail(user.email)) {
			throw BadRequestException(propertiesMessage.getProperty("message.auth.email-registered"))
		}

		user.password = passwordEncoder.encode(temporalPassword)
		user.enabled = true
		user.photo = "${getHost(servletRequest)}/rest/auth/generate-profile-image/${user.name[0]}/" +
			URLEncoder.encode(color.color, StandardCharsets.UTF_8.toString()) +
			"/" + URLEncoder.encode(color.background, StandardCharsets.UTF_8.toString())
		user.accountType = AccountType.DEFAULT.name

		val userOut = authRepository.save(user)

		authMail.sendActivateAccountMail(userOut)

		return response.created(propertiesMessage.getProperty("message.auth.account-created"))
	}

	@Transactional
	override fun signIn(request: Request): ResponseEntity<Any> {
		val user = request.to<IUser>(
			userEntity,
			Validations(
				Validation(
					"userName",
					propertiesMessage.getProperty("message.auth.user-name-required"),
					ValidationType.NOT_NULL,
					ValidationType.NOT_BLANK
				),
				Validation(
					"password",
					propertiesMessage.getProperty("message.auth.password-required"),
					ValidationType.NOT_NULL,
					ValidationType.NOT_BLANK
				)
			)
		)
		val userOut = authRepository.findByUserNameOrEmail(
			user.userName
		).orElseThrow { throw BadRequestException(propertiesMessage.getProperty("message.auth.user-not-fount")) }

		if (!userOut.active) {
			throw BadRequestException(propertiesMessage.getProperty("message.auth.account-not-activate"))
		}

		if (!userOut.enabled) {
			throw BadRequestException(propertiesMessage.getProperty("message.auth.account-blocked"))
		}

		val session: Map<String, Any>

		try {
			val authentication: Authentication = authenticationManager.authenticate(
				UsernamePasswordAuthenticationToken(
					userOut.userName,
					user.password
				)
			)

			session = jwtProvider.generateJwtTokenRefresh(authentication)
		} catch (e: Exception) {
			throw UnauthenticatedException(propertiesMessage.getProperty("message.auth.password-incorrect"))
		}

		val out = response.toMap(
			userOut
		)

		out["session"] = session

		return out
			.exclude(
				"password",
				"enabled",
				"active",
				"activatePassword",
				"refreshToken"
			)
			.firstId()
			.ok()
	}

	@Transactional
	override fun signInFormCode(request: Request, servletRequest: HttpServletRequest): ResponseEntity<Any> {
		request.to<LinkedHashMap<String, String>>(
			LinkedHashMap::class,
			Validations(
				Validation(
					"code",
					"Upss el codigo de acceso es necesario para iniciar sesión",
					ValidationType.NOT_NULL,
					ValidationType.NOT_BLANK
				),
				Validation(
					"type",
					"Upss el tipo de codigo es necesario, los tipos aceptados son 'Google' u 'Outlook'",
					ValidationType.NOT_NULL,
					ValidationType.NOT_BLANK,
					ValidationType.includes("Google", "Outlook")
				)
			)
		)
		val userLogged = if (request["type"].toString() == "Google")
			googleAuthentication.signIn(request["code"].toString()) else
			outlookAuthentication.signIn(request["code"].toString())
		var userSearched = authRepository.findByUserNameOrEmail(userLogged.email).orElse(null)
		val password = UUID.randomUUID().toString()
		val session: Map<String, Any>

		if (userSearched == null) {
			val user = userEntity.java.getDeclaredConstructor().newInstance() as IUser
			val lastNames = userLogged.lastName.split(" ")
			val color = Color().getMaterialColor(CategoryColor.MATERIAL_500)

			user.name = userLogged.firstName
			user.surname = lastNames[0]
			user.motherSurname = if (lastNames.size > 1) lastNames[1] else ""
			user.userName = "${userLogged.firstName.replace(" ", "")}-${UUID.randomUUID()}"
			user.password = passwordEncoder.encode(password)
			user.email = userLogged.email
			user.active = true
			user.enabled = true
			user.photo = "${getHost(servletRequest)}/rest/auth/generate-profile-image/${user.name[0]}/" +
				URLEncoder.encode(color.color, StandardCharsets.UTF_8.toString()) +
				"/" + URLEncoder.encode(color.background, StandardCharsets.UTF_8.toString())
			user.accountType = if (request["type"].toString() == "Google")
				AccountType.GMAIL.name else AccountType.OUTLOOK.name

			userSearched = authRepository.save(user)
		} else {
			userSearched.password = passwordEncoder.encode(password)
			userSearched = authRepository.save(userSearched)
		}

		try {
			val authentication: Authentication = authenticationManager.authenticate(
				UsernamePasswordAuthenticationToken(
					userSearched.userName,
					password
				)
			)

			session = jwtProvider.generateJwtTokenRefresh(authentication)
		} catch (e: Exception) {
			throw UnauthenticatedException(propertiesMessage.getProperty("message.auth.password-incorrect"))
		}

		val out = response.toMap(userSearched)

		out["session"] = session

		return out
			.exclude(
				"password",
				"enabled",
				"active",
				"activatePassword",
				"refreshToken"
			)
			.firstId()
			.ok()
	}

	@Transactional(readOnly = true)
	override fun refreshToken(request: Request): ResponseEntity<Any> {
		if (!request.containsKey("refreshToken")) {
			throw BadRequestException(propertiesMessage.getProperty("message.auth.refresh-token-required"))
		}

		request["refreshToken"].toString().ifEmpty {
			throw BadRequestException(propertiesMessage.getProperty("message.auth.refresh-token-required"))
		}

		return response.ok(
			jwtProvider.refreshToken(
				request["refreshToken"].toString()
			)
		)
	}

	@Transactional(readOnly = true)
	override fun generateProfileImage(
		initialLetter: Char,
		color: String,
		background: String
	): ResponseEntity<Any> {
		return response.file(
			"image/png",
			"userprofile.png",
			avatar.generateUserImage(initialLetter, color, background)
		)
	}

	private fun getHost(servletRequest: HttpServletRequest): String {
		return if (servletRequest.localAddr.contains("0:0:0"))
			"http://localhost:${servletRequest.localPort}" else
			"http://${servletRequest.localAddr}:${servletRequest.localPort}"
	}

}