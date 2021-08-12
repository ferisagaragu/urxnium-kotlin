package org.pechblenda.auth.service

import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.client.j2se.MatrixToImageWriter

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
import org.pechblenda.auth.enum.AccountType

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ClassPathResource

import kotlin.reflect.KClass

import java.net.URLEncoder
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.UUID
import java.util.Date
import javax.servlet.http.HttpServletRequest
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import javax.imageio.ImageIO
import javax.servlet.http.HttpServletResponse
import kotlin.collections.LinkedHashMap
import org.apache.commons.io.output.ByteArrayOutputStream
import org.springframework.core.io.InputStreamResource

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

	@Value("\${app.host:}")
	private lateinit var hostName: String

	@Value("\${app.auth.front-base-url:}")
	private lateinit var redirectUri: String

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

	override fun generateQRAuthentication(servletRequest: HttpServletRequest): ResponseEntity<Any> {
		val matrix = MultiFormatWriter().encode(
			"${getHost(servletRequest)}/rest/auth/sign-in-qr-view/${jwtProvider.generateJwtSecretToken()}",
			BarcodeFormat.QR_CODE,
			512,
			512
		)
		val os = ByteArrayOutputStream()
		ImageIO.write(MatrixToImageWriter.toBufferedImage(matrix), "png", os)
		val iss: InputStream = ByteArrayInputStream(os.toByteArray())

		return response.file("image/png", "${UUID.randomUUID()}.png", iss)
	}

	@Transactional(readOnly = true)
	override fun findBasicUserInfoByUuid(userUuid: UUID): ResponseEntity<Any> {
		val user = authRepository.likeByUserName("%$userUuid").orElse(null) ?: return response.ok()

		return response.toMap(user)
			.include(
				"name",
				"surname",
				"motherSurname",
				"email"
			).ok()
	}

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
		val color = Color().getMaterialColor(CategoryColor.MATERIAL_500)

		if (authRepository.existsByUserName(user.userName)) {
			throw BadRequestException(authMessage.getUserNameRegistered())
		}

		if (authRepository.existsByEmail(user.email)) {
			throw BadRequestException(authMessage.getEmailRegistered())
		}

		user.password = passwordEncoder.encode(temporalPassword)
		user.enabled = true
		user.photo = "${getHost(servletRequest)}/rest/auth/generate-profile-image/${user.name[0]}/" +
			URLEncoder.encode(color.color, StandardCharsets.UTF_8.toString()) +
			"/" + URLEncoder.encode(color.background, StandardCharsets.UTF_8.toString())
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
				"Upps el secret no es valido",
				ValidationType.NOT_NULL,
				ValidationType.NOT_BLANK,
				ValidationType.EXIST
			),
			Validation(
				"uuid",
				"Upps el uuid es requerido",
				ValidationType.NOT_NULL,
				ValidationType.NOT_BLANK,
				ValidationType.EXIST
			)
		))

		val code = jwtProvider.validateJwtSecretToken(request["secret"].toString())
		val userFind = authRepository.likeByUserName("%${request["uuid"].toString()}").orElse(null)

		if (authRepository.findByPassword(code).orElse(null) != null) {
			throw BadRequestException("Upps el código de inicio de sesión ya esta registrado")
		}

		if (userFind == null) {
			request.validate(Validations(
				Validation(
					"name",
					"Upps el nombre es requerido",
					ValidationType.NOT_NULL,
					ValidationType.NOT_BLANK,
					ValidationType.EXIST
				),
				Validation(
					"surname",
					"Upps apellido es requerido",
					ValidationType.NOT_NULL,
					ValidationType.NOT_BLANK,
					ValidationType.EXIST
				),
				Validation(
					"email",
					"Upps email es requerido",
					ValidationType.NOT_NULL,
					ValidationType.NOT_BLANK,
					ValidationType.EXIST
				)
			))
			val user = userEntity.java.getDeclaredConstructor().newInstance() as IUser
			val color = Color().getMaterialColor(CategoryColor.MATERIAL_500)

			if (authRepository.existsByEmail(request["email"].toString())) {
				throw BadRequestException("Upps el correo electrónico ya esta registrado")
			}

			user.name = request["name"].toString()
			user.surname = request["surname"].toString()
			user.motherSurname = request["motherSurname"].toString()
			user.userName = "${request["name"].toString().replace(" ", "")}&" +
					"${UUID.fromString(request["uuid"].toString())}"
			user.email = request["email"].toString()
			user.password = code
			user.active = true
			user.enabled = true
			user.photo = "${getHost(servletRequest)}/rest/auth/generate-profile-image/${user.name[0]}/" +
					URLEncoder.encode(color.color, StandardCharsets.UTF_8.toString()) +
					"/" + URLEncoder.encode(color.background, StandardCharsets.UTF_8.toString())
			user.accountType = AccountType.QR.name

			authRepository.save(user)
		} else {
			userFind.password = code
		}

		val out = Request()
		out["code"] = code

		return this.response.ok(out)
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
				"Upps el codigo es requerido para inciar sesión",
				ValidationType.NOT_NULL,
				ValidationType.NOT_BLANK,
				ValidationType.EXIST
			)
		))

		val user = authRepository.findByPassword(request["code"].toString()).orElseThrow {
			UnauthenticatedException("Upps el codigo que ingresaste no es valido")
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
		return response.file(
			"image/png",
			"userprofile.png",
			avatar.generateUserImage(initialLetter, color, background)
		)
	}

	private fun getHost(servletRequest: HttpServletRequest): String {
		if (hostName.isNotBlank()) {
			return hostName
		}

		return if (servletRequest.localAddr.contains("0:0:0"))
			"http://localhost:${servletRequest.localPort}" else
			"http://${servletRequest.localAddr}:${servletRequest.localPort}"
	}

}