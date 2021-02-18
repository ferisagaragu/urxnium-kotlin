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
import org.pechblenda.service.Request
import org.pechblenda.service.Response
import org.pechblenda.service.helper.Validation
import org.pechblenda.service.helper.ValidationType
import org.pechblenda.service.helper.Validations
import org.pechblenda.util.Avatar
import org.pechblenda.util.Color

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

import java.util.Properties
import java.util.UUID

import kotlin.reflect.KClass

import java.net.URLEncoder

import java.nio.charset.StandardCharsets

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
	override fun validateToken(): ResponseEntity<Any> {
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

		val request = Request()
		request["validateToken"] = true

		return response.ok(request)
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
	override fun signUp(request: Request): ResponseEntity<Any> {
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
		val color = Color().getHexRandomColorAndBackground()

		if (authRepository.existsByUserName(user.userName)) {
			throw BadRequestException(propertiesMessage.getProperty("message.auth.user-name-registered"))
		}

		if (authRepository.existsByEmail(user.email)) {
			throw BadRequestException(propertiesMessage.getProperty("message.auth.email-registered"))
		}

		user.password = passwordEncoder.encode(temporalPassword)
		user.enabled = true
		user.photo = "http://localhost:5000/api/auth/generate-profile-image/${user.name[0]}/" +
			URLEncoder.encode(color["color"], StandardCharsets.UTF_8.toString()) +
			"/" + URLEncoder.encode(color["background"], StandardCharsets.UTF_8.toString())

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
		lyrics: String,
		color: String,
		background: String
	): ResponseEntity<Any> {
		return response.file(
			"image/png",
			"userprofile.png",
			avatar.createDefaultAccountImage(lyrics, color, background)
		)
	}

}