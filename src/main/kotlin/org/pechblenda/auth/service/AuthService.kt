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
import org.pechblenda.auth.enum.AccountType
import org.pechblenda.core.shared.Server
import org.pechblenda.core.shared.DynamicResources
import org.pechblenda.service.helper.SingleValidation

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.transaction.annotation.Transactional
import org.springframework.core.io.InputStreamResource
import org.springframework.stereotype.Service
import org.springframework.beans.factory.annotation.Value

import kotlin.reflect.KClass

import java.util.UUID
import java.util.Date
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.collections.LinkedHashMap
import kotlin.random.Random

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

	@Autowired
	private lateinit var server: Server

	@Autowired
	private lateinit var dynamicResources: DynamicResources

	@Value("\${app.user-avatar:material}")
	private lateinit var avatarType: String

	private val authRepository: IAuthRepository<IUser, UUID>
	private val userEntity: KClass<*>

	constructor(authRepository: Any, userEntity: KClass<*>) {
		this.authRepository = authRepository as IAuthRepository<IUser, UUID>
		this.userEntity = userEntity
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
			throw BadRequestException(authMessage.getActivateUserInvalid())
		}

		val out = Request()
		out["canActivate"] = true

		return response.ok(out)
	}

	@Transactional(readOnly = true)
	override fun canChangePassword(activatePassword: UUID): ResponseEntity<Any> {
		if (!authRepository.existsByActivatePassword(activatePassword)) {
			throw BadRequestException(authMessage.getRecoverCodeInvalid())
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

	@Transactional(readOnly = true)
	override fun generateQRAuthentication(servletRequest: HttpServletRequest): ResponseEntity<Any> {
		return response.qr(
			"${server.getHost(servletRequest)}/rest/auth/sign-in-qr-view/#/${jwtProvider.generateJwtSecretToken()}"
		)
	}

	@Transactional(readOnly = true)
	override fun findBasicUserInfoByUuid(userUuid: UUID): ResponseEntity<Any> {
		val user = authRepository.likeByUserName("%$userUuid").orElse(null)
			?: return response.ok()

		return response.toMap(user)
			.include(
				"name",
				"surname",
				"motherSurname",
				"email",
				"active"
			).ok()
	}

	@Transactional(readOnly = true)
	override fun signInQRView(response: HttpServletResponse): ResponseEntity<Any> {
		return Response().file(
			"text/html",
			"index.html",
			InputStreamResource(
				this.javaClass.classLoader.getResourceAsStream(
					"qr/index.html"
				)
			)
		)
	}

	@Transactional
	override fun activateAccount(request: Request): ResponseEntity<Any> {
		val user = request.to<IUser>(
			userEntity,
			Validations(
				Validation(
					"password",
					authMessage.getPasswordRequired(),
					ValidationType.NOT_NULL,
					ValidationType.NOT_BLANK
				)
			)
		)

		val findUser = authRepository.findById(user.uuid).orElseThrow {
			throw BadRequestException(authMessage.getUserNotFount())
		}

		if (findUser.active) {
			throw BadRequestException(authMessage.getAccountBeActivated())
		}

		findUser.password = passwordEncoder.encode(user.password)
		findUser.active = true

		return response.ok(authMessage.getAccountActivated())
	}

	@Transactional
	override fun activateQRAccount(request: Request): ResponseEntity<Any> {
		val secret = request.to<String>(
			"secret",
			SingleValidation(
				authMessage.getActivateQRUserSecretRequired(),
				ValidationType.NOT_NULL,
				ValidationType.NOT_BLANK,
				ValidationType.EXIST
			)
		)
		val verifyCode = request.to<String>(
			"verifyCode",
			SingleValidation(
				authMessage.getActivateQRUserVerifyCodeRequired(),
				ValidationType.NOT_NULL,
				ValidationType.NOT_BLANK,
				ValidationType.EXIST
			)
		)

		val code = jwtProvider.validateJwtSecretToken(secret)
		val userFind = authRepository.findByPassword(verifyCode).orElseThrow {
			UnauthenticatedException(authMessage.getActivateQRUserVerifyCodeInvalid())
		}

		userFind.active = true
		userFind.enabled = true
		userFind.password = code

		val out = Request()
		out["code"] = code

		return response.ok(out)
	}

	@Transactional
	override fun changePassword(request: Request): ResponseEntity<Any> {
		val user = request.to<IUser>(
			userEntity,
			Validations(
				Validation(
					"password",
					authMessage.getPasswordRequired(),
					ValidationType.NOT_NULL,
					ValidationType.NOT_BLANK
				)
			)
		)
		val userFind = authRepository.findByActivatePassword(user.activatePassword!!).orElseThrow {
			throw BadRequestException(authMessage.getAccountNotMatch())
		}

		if (!userFind.active) {
			throw BadRequestException(authMessage.getAccountNotActivate())
		}

		userFind.activatePassword = null
		userFind.password = passwordEncoder.encode(user.password)

		return response.ok(authMessage.getPasswordChanged())
	}

	@Transactional
	override fun changeQRDevice(request: Request): ResponseEntity<Any> {
		val activatePassword = request.to<UUID>(
			"activatePassword",
			SingleValidation(
				authMessage.getSignUpQRUuidRequired(),
				ValidationType.NOT_NULL,
				ValidationType.NOT_BLANK
			)
		)
		val deviceUuid = request.to<UUID>(
			"deviceUuid",
			SingleValidation(
				authMessage.getSignUpQRUuidRequired(),
				ValidationType.NOT_NULL,
				ValidationType.NOT_BLANK
			)
		)

		val userFind = authRepository.findByActivatePassword(activatePassword).orElseThrow {
			BadRequestException(authMessage.getUserNotFount())
		}

		if (!userFind.active) {
			BadRequestException(authMessage.getAccountNotActivate())
		}

		if (!userFind.enabled) {
			BadRequestException(authMessage.getAccountBlocked())
		}

		val code = "${Random.nextInt(0, 9)}${Random.nextInt(0, 9)} - " +
				"${Random.nextInt(0, 9)}${Random.nextInt(0, 9)} - " +
				"${Random.nextInt(0, 9)}${Random.nextInt(0, 9)}"

		if (userFind.accountType == AccountType.QR.name) {
			val name = userFind.userName.split('-')[0]
			userFind.userName = "$name-${deviceUuid}"
			userFind.activatePassword = null
			userFind.password = code
		}

		val out = Request()
		out["code"] = code

		return response.ok(out)
	}

	@Transactional
	override fun recoverPassword(request: Request): ResponseEntity<Any> {
		val user = request.to<IUser>(
			userEntity,
			Validations(
				Validation(
					"email",
					authMessage.getEmailRequired(),
					ValidationType.NOT_NULL,
					ValidationType.NOT_BLANK
				)
			)
		)
		val userFind = authRepository.findByUserNameOrEmail(user.email).orElseThrow {
			BadRequestException(authMessage.getAccountNotMatch())
		}

		if (!userFind.active) {
			throw BadRequestException(authMessage.getAccountNotActivate())
		}

		if (userFind.accountType != AccountType.DEFAULT.name) {
			throw BadRequestException(authMessage.getAccountTypeNotRecover(userFind.accountType))
		}

		userFind.activatePassword = UUID.randomUUID()

		authMail.sendRecoverPasswordMail(userFind)

		return response.ok(
			authMessage.getRecoverInstruction().replace(
				"{email}",
				userFind.email
			)
		)
	}

	@Transactional
	override fun recoverQRAccount(
		request: Request,
		servletRequest: HttpServletRequest
	): ResponseEntity<Any> {
		val email = request.to<String>(
			"email",
			SingleValidation(
				authMessage.getEmailRequired(),
				ValidationType.NOT_NULL,
				ValidationType.NOT_BLANK
			)
		)

		val userFind = authRepository.findByUserNameOrEmail(email).orElseThrow {
			BadRequestException(authMessage.getUserNotFount())
		}

		if (!userFind.active) {
			BadRequestException(authMessage.getAccountNotActivate())
		}

		if (!userFind.enabled) {
			BadRequestException(authMessage.getAccountBlocked())
		}

		userFind.activatePassword = UUID.randomUUID()
		authMail.sendRecoverQRAccountMail(userFind, servletRequest)

		return response.ok()
	}

	@Transactional
	override fun signUp(request: Request, servletRequest: HttpServletRequest): ResponseEntity<Any> {
		val user = request.to<IUser>(
			userEntity,
			Validations(
				Validation(
					"name",
					authMessage.getNameRequired(),
					ValidationType.NOT_NULL,
					ValidationType.NOT_BLANK
				),
				Validation(
					"surname",
					authMessage.getSurnameRequired(),
					ValidationType.NOT_NULL,
					ValidationType.NOT_BLANK
				),
				Validation(
					"motherSurname",
					authMessage.getMotherSurnameRequired(),
					ValidationType.NOT_NULL,
					ValidationType.NOT_BLANK
				),
				Validation(
					"userName",
					authMessage.getUserNameRequired(),
					ValidationType.NOT_NULL,
					ValidationType.NOT_BLANK
				),
				Validation(
					"email",
					authMessage.getEmailRequired(),
					ValidationType.NOT_NULL,
					ValidationType.NOT_BLANK
				)
			)
		)
		val temporalPassword = UUID.randomUUID().toString()

		if (authRepository.existsByUserName(user.userName)) {
			throw BadRequestException(authMessage.getUserNameRegistered())
		}

		if (authRepository.existsByEmail(user.email)) {
			throw BadRequestException(authMessage.getEmailRegistered())
		}

		user.password = passwordEncoder.encode(temporalPassword)
		user.enabled = true
		user.photo = dynamicResources.getUserImageUrl(servletRequest, user)
		user.accountType = AccountType.DEFAULT.name

		val userOut = authRepository.save(user)

		authMail.sendActivateAccountMail(userOut)

		return response.created(authMessage.getAccountCreated())
	}

	@Transactional
	override fun signUpQR(request: Request, servletRequest: HttpServletRequest): ResponseEntity<Any> {
		request.validate(Validations(
			Validation(
				"secret",
				authMessage.getActivateQRUserSecretRequired(),
				ValidationType.NOT_NULL,
				ValidationType.NOT_BLANK,
				ValidationType.EXIST
			),
			Validation(
				"uuid",
				authMessage.getSignUpQRUuidRequired(),
				ValidationType.NOT_NULL,
				ValidationType.NOT_BLANK,
				ValidationType.EXIST
			)
		))

		val code = jwtProvider.validateJwtSecretToken(request["secret"].toString())
		val userFind = authRepository.likeByUserName("%${request["uuid"].toString()}").orElse(null)

		if (authRepository.findByPassword(code).orElse(null) != null) {
			throw BadRequestException(authMessage.getSignUpQRCodeRegistered())
		}

		if (userFind == null) {
			request.validate(Validations(
				Validation(
					"name",
					authMessage.getNameRequired(),
					ValidationType.NOT_NULL,
					ValidationType.NOT_BLANK,
					ValidationType.EXIST
				),
				Validation(
					"surname",
					authMessage.getSurnameRequired(),
					ValidationType.NOT_NULL,
					ValidationType.NOT_BLANK,
					ValidationType.EXIST
				),
				Validation(
					"email",
					authMessage.getEmailRequired(),
					ValidationType.NOT_NULL,
					ValidationType.NOT_BLANK,
					ValidationType.EXIST
				)
			))
			val user = userEntity.java.getDeclaredConstructor().newInstance() as IUser

			if (authRepository.existsByEmail(request["email"].toString())) {
				throw BadRequestException(authMessage.getEmailRegistered(), "duplicate")
			}

			user.name = request["name"].toString()
			user.surname = request["surname"].toString()
			user.motherSurname = request["motherSurname"].toString()
			user.userName = "${request["name"].toString().replace(" ", "")}-" +
					"${UUID.fromString(request["uuid"].toString())}"
			user.email = request["email"].toString()
			user.password = "${Random.nextInt(0, 9)}${Random.nextInt(0, 9)} - " +
					"${Random.nextInt(0, 9)}${Random.nextInt(0, 9)} - " +
					"${Random.nextInt(0, 9)}${Random.nextInt(0, 9)}"
			user.active = false
			user.enabled = false
			user.photo = dynamicResources.getUserImageUrl(servletRequest, user)
			user.accountType = AccountType.QR.name

			val userSaved = authRepository.save(user)
			authMail.sendActivateQRAccountMail(userSaved)
			return response.ok()
		} else {
			if (!userFind.active) {
				throw BadRequestException(authMessage.getAccountNotActive())
			}

			if (!userFind.enabled) {
				BadRequestException(authMessage.getAccountBlocked())
			}

			userFind.password = code
		}

		val out = Request()
		out["code"] = code

		return response.ok(out)
	}

	@Transactional
	override fun signIn(request: Request): ResponseEntity<Any> {
		val user = request.to<IUser>(
			userEntity,
			Validations(
				Validation(
					"userName",
					authMessage.getUserNameRequired(),
					ValidationType.NOT_NULL,
					ValidationType.NOT_BLANK
				),
				Validation(
					"password",
					authMessage.getPasswordRequired(),
					ValidationType.NOT_NULL,
					ValidationType.NOT_BLANK
				)
			)
		)
		val userOut = authRepository.findByUserNameOrEmail(
			user.userName
		).orElseThrow { throw BadRequestException(authMessage.getUserNotFount()) }

		if (!userOut.active) {
			throw BadRequestException(authMessage.getAccountNotActivate())
		}

		if (!userOut.enabled) {
			throw BadRequestException(authMessage.getAccountBlocked())
		}

		if (userOut.accountType != AccountType.DEFAULT.name) {
			throw UnauthenticatedException(authMessage.getAccountTypeNotValid(userOut.accountType))
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
			throw UnauthenticatedException(authMessage.getPasswordIncorrect())
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
					authMessage.getAccessCodeRequired(),
					ValidationType.NOT_NULL,
					ValidationType.NOT_BLANK
				),
				Validation(
					"type",
					authMessage.getTypeCodeRequired(),
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

			user.name = userLogged.firstName
			user.surname = lastNames[0]
			user.motherSurname = if (lastNames.size > 1) lastNames[1] else ""
			user.userName = "${userLogged.firstName.replace(" ", "")}-${UUID.randomUUID()}"
			user.password = passwordEncoder.encode(password)
			user.email = userLogged.email
			user.active = true
			user.enabled = true
			user.photo = dynamicResources.getUserImageUrl(servletRequest, user)
			user.accountType = if (request["type"].toString() == "Google")
				AccountType.GMAIL.name else AccountType.OUTLOOK.name

			userSearched = authRepository.save(user)
		} else {
			if (userSearched.accountType == AccountType.DEFAULT.name) {
				throw UnauthenticatedException(authMessage.getAccountRegistered())
			}

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
			throw UnauthenticatedException(authMessage.getPasswordIncorrect())
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

	@Transactional
	override fun signInFormQR(request: Request): ResponseEntity<Any> {
		request.validate(Validations(
			Validation(
				"code",
				authMessage.getAccessCodeRequired(),
				ValidationType.NOT_NULL,
				ValidationType.NOT_BLANK,
				ValidationType.EXIST
			)
		))

		val user = authRepository.findByPassword(request["code"].toString()).orElseThrow {
			UnauthenticatedException(authMessage.getSignUpQRCodeInvalid())
		}
		val session: Map<String, Any>
		val password = UUID.randomUUID()

		user.password = passwordEncoder.encode(password.toString())

		try {
			val authentication: Authentication = authenticationManager.authenticate(
				UsernamePasswordAuthenticationToken(
					user.userName,
					password
				)
			)

			session = jwtProvider.generateJwtTokenRefresh(authentication)
		} catch (e: Exception) {
			throw UnauthenticatedException(authMessage.getPasswordIncorrect())
		}

		val out = response.toMap(user)

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
			throw BadRequestException(authMessage.getRefreshTokenRequired())
		}

		request["refreshToken"].toString().ifEmpty {
			throw BadRequestException(authMessage.getRefreshTokenRequired())
		}

		val user = authRepository.findByUserName(
			jwtProvider.getUserNameFromJwtToken(request["refreshToken"].toString())
				.replace("_refresh", "")
		).orElseThrow {
			UnauthenticatedException(authMessage.getUserNotFount())
		}

		if (!user.enabled || !user.active) {
			throw UnauthenticatedException(authMessage.getAccountBlocked())
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
		println(avatarType)

		return if (avatarType == "material") {
			response.file(
				"image/png",
				"userprofile.png",
				avatar.generateUserImage(initialLetter, color, background)
			)
		} else {
			response.file(
				"image/png",
				"userprofile.png",
				avatar.generateGradientImage(initialLetter, color, color, background)
			)
		}
	}

}