package org.pechblenda.auth

import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.UUID
import java.util.stream.Collectors
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.pechblenda.auth.service.AuthService
import org.pechblenda.doc.annotation.ApiDocumentation
import org.pechblenda.exception.HttpExceptionResponse
import org.pechblenda.service.Request

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException


@CrossOrigin(methods = [
	RequestMethod.GET,
	RequestMethod.POST
])
@RestController
@RequestMapping(name = "Auth", value = ["/rest/auth"])
class AuthController {

	@Autowired
	private lateinit var authService: AuthService

	@Autowired
	private lateinit var httpExceptionResponse: HttpExceptionResponse

	@Value("\${app.host:}")
	private lateinit var hostName: String

	@GetMapping("/validate-token")
	@ApiDocumentation(path = "api/assets/auth/validate-token.json")
	fun validateToken(
		@RequestHeader("Authorization") authorization: String
	): ResponseEntity<Any> {
		return try {
			authService.validateToken(authorization)
		} catch (e: ResponseStatusException) {
			httpExceptionResponse.error(e)
		}
	}

	@GetMapping("/can-activate-account/{userUid}")
	@ApiDocumentation(path = "api/assets/auth/can-activate-account.json")
	fun canActivate(
		@PathVariable("userUid") userUid: UUID
	): ResponseEntity<Any> {
		return try {
			authService.canActivate(userUid)
		} catch (e: ResponseStatusException) {
			httpExceptionResponse.error(e)
		}
	}

	@GetMapping("/can-change-password/{activatePassword}")
	@ApiDocumentation(path = "api/assets/auth/can-change-password.json")
	fun canChangePassword(
		@PathVariable("activatePassword") activatePassword: UUID
	): ResponseEntity<Any> {
		return try {
			authService.canChangePassword(activatePassword)
		} catch (e: ResponseStatusException) {
			httpExceptionResponse.error(e)
		}
	}

	@GetMapping("/generate-profile-image/{initialLetter}/{color}/{background}")
	@ApiDocumentation(path = "api/assets/auth/generate-profile-image.json")
	fun generateProfileImage(
		@PathVariable("initialLetter") initialLetter: Char,
		@PathVariable("color") color: String,
		@PathVariable("background") background: String
	): ResponseEntity<Any> {
		return authService.generateProfileImage(initialLetter, color, background)
	}

	@GetMapping("/generate-google-authentication-url")
	@ApiDocumentation(path = "api/assets/auth/generate-google-authentication-url.json")
	fun generateGoogleAuthenticationUrl(): ResponseEntity<Any> {
		return try {
			return authService.generateGoogleAuthenticationUrl()
		} catch (e: ResponseStatusException) {
			httpExceptionResponse.error(e)
		}
	}

	@GetMapping("/generate-outlook-authentication-url")
	@ApiDocumentation(path = "api/assets/auth/generate-outlook-authentication-url.json")
	fun generateOutlookAuthenticationUrl(): ResponseEntity<Any> {
		return try {
			return authService.generateOutlookAuthenticationUrl()
		} catch (e: ResponseStatusException) {
			httpExceptionResponse.error(e)
		}
	}

	@GetMapping("/generate-qr-authentication")
	@ApiDocumentation(path = "qr/assets/auth/generate-qr-authentication.json")
	fun generateQRAuthentication(servletRequest: HttpServletRequest): ResponseEntity<Any> {
		return try {
			return authService.generateQRAuthentication(servletRequest)
		} catch (e: ResponseStatusException) {
			httpExceptionResponse.error(e)
		}
	}

	@GetMapping("/find-basic-user-info-by-uuid/{userUuid}")
	@ApiDocumentation(path = "qr/assets/auth/find-basic-user-info-by-uuid.json")
	fun findBasicUserInfoByUuid(
		@PathVariable("userUuid") userUuid: UUID
	): ResponseEntity<Any> {
		return try {
			return authService.findBasicUserInfoByUuid(userUuid)
		} catch (e: ResponseStatusException) {
			httpExceptionResponse.error(e)
		}
	}

	@GetMapping("/sign-in-qr-view/{secret}")
	@ApiDocumentation(path = "qr/assets/auth/sign-in-qr-view.json")
	fun signInQRView(
		response: HttpServletResponse,
		@PathVariable("secret") secret: String
	): String {
		val resource = BufferedReader(
			InputStreamReader(this.javaClass.classLoader.getResourceAsStream("qr/index.html"), StandardCharsets.UTF_8)
		).lines().collect(Collectors.joining("\n")).toString().replace("\n", "")

		response.contentType = "text/html";
		response.characterEncoding = "UTF-8";
		return resource
	}

	@PostMapping("/activate-account")
	@ApiDocumentation(path = "api/assets/auth/activate-account.json")
	fun activateAccount(
		@RequestBody request: Request
	): ResponseEntity<Any> {
		return try {
			authService.activateAccount(request)
		} catch (e: ResponseStatusException) {
			httpExceptionResponse.error(e)
		}
	}

	@PostMapping("/change-password")
	@ApiDocumentation(path = "api/assets/auth/change-password.json")
	fun changePassword(
		@RequestBody request: Request
	): ResponseEntity<Any> {
		return try {
			authService.changePassword(request)
		} catch (e: ResponseStatusException) {
			httpExceptionResponse.error(e)
		}
	}

	@PostMapping("/recover-password")
	@ApiDocumentation(path = "api/assets/auth/recover-password.json")
	fun recoverPassword(
		@RequestBody request: Request
	): ResponseEntity<Any> {
		return try {
			authService.recoverPassword(request)
		} catch (e: ResponseStatusException) {
			httpExceptionResponse.error(e)
		}
	}

	@PostMapping("/sign-up")
	@ApiDocumentation(path = "api/assets/auth/sign-up.json")
	fun signUp(
		@RequestBody request: Request,
		servletRequest: HttpServletRequest
	): ResponseEntity<Any> {
		return try {
			authService.signUp(request, servletRequest)
		} catch (e: ResponseStatusException) {
			httpExceptionResponse.error(e)
		}
	}

	@PostMapping("/sign-up-qr")
	@ApiDocumentation(path = "qr/assets/auth/sign-up-qr.json")
	fun signUpQR(
		@RequestBody request: Request,
		servletRequest: HttpServletRequest
	): ResponseEntity<Any> {
		return try {
			authService.signUpQR(request, servletRequest)
		} catch (e: ResponseStatusException) {
			httpExceptionResponse.error(e)
		}
	}

	@PostMapping("/sign-in")
	@ApiDocumentation(path = "api/assets/auth/sign-in.json")
	fun signIn(
		@RequestBody request: Request
	): ResponseEntity<Any> {
		return try {
			authService.signIn(request)
		} catch (e: ResponseStatusException) {
			httpExceptionResponse.error(e)
		}
	}

	@PostMapping("/sign-in-form-code")
	@ApiDocumentation(path = "api/assets/auth/sign-in-form-code.json")
	fun signInFormCode(
		@RequestBody request: Request,
		servletRequest: HttpServletRequest
	): ResponseEntity<Any> {
		return try {
			authService.signInFormCode(request, servletRequest)
		} catch (e: ResponseStatusException) {
			httpExceptionResponse.error(e)
		}
	}

	@PostMapping("/sign-in-form-qr")
	@ApiDocumentation(path = "qr/assets/auth/sign-in-form-qr.json")
	fun signInFormQR(
		@RequestBody request: Request
	): ResponseEntity<Any> {
		return try {
			authService.signInFormQR(request)
		} catch (e: ResponseStatusException) {
			httpExceptionResponse.error(e)
		}
	}

	@PostMapping("/refresh-token")
	@ApiDocumentation(path = "api/assets/auth/refresh-token.json")
	fun refreshToken(
		@RequestBody request: Request
	): ResponseEntity<Any> {
		return try {
			authService.refreshToken(request)
		} catch (e: ResponseStatusException) {
			httpExceptionResponse.error(e)
		}
	}

	//Resources
	@RequestMapping("/sign-in-qr-view/styles.c6aadb6bdca66db79bc4.css")
	fun getStyles(response: HttpServletResponse): String {
		val text: String = BufferedReader(
			InputStreamReader(
				this.javaClass.classLoader.getResourceAsStream("qr/styles.c6aadb6bdca66db79bc4.css"),
				StandardCharsets.UTF_8
			)
		).lines().collect(Collectors.joining(""))

		response.contentType = "text/plain"
		response.characterEncoding = "UTF-8"
		return text
	}

	@RequestMapping(value = ["/sign-in-qr-view/runtime.4ec9417e38772e2a3b1f.js"], produces = ["application/javascript"])
	fun getRuntime(response: HttpServletResponse): String {
		val text: String = BufferedReader(
			InputStreamReader(
				this.javaClass.classLoader.getResourceAsStream(
					"qr/runtime.4ec9417e38772e2a3b1f.js"
				),
				StandardCharsets.UTF_8
			)
		).lines().collect(Collectors.joining(""))

		response.contentType = "application/javascript"
		response.characterEncoding = "UTF-8"
		return text
	}

	@RequestMapping(value = ["/sign-in-qr-view/polyfills.c8539b94e427590b980e.js"], produces = ["application/javascript"])
	fun getPolyfills(response: HttpServletResponse): String {
		val text: String = BufferedReader(
			InputStreamReader(
				this.javaClass.classLoader.getResourceAsStream(
					"qr/polyfills.c8539b94e427590b980e.js"
				),
				StandardCharsets.UTF_8
			)
		).lines().collect(Collectors.joining(""))

		response.contentType = "application/javascript"
		response.characterEncoding = "UTF-8"
		return text
	}

	@RequestMapping(value = ["/sign-in-qr-view/main.7cf7c9bc6068a4f2bea2.js"], produces = ["application/javascript"])
	fun getMain(response: HttpServletResponse): String {
		val text: String = BufferedReader(
			InputStreamReader(
				this.javaClass.classLoader.getResourceAsStream(
					"qr/main.7cf7c9bc6068a4f2bea2.js"
				),
				StandardCharsets.UTF_8
			)
		).lines().collect(Collectors.joining(""))

		response.contentType = "application/javascript"
		response.characterEncoding = "UTF-8"
		return text
	}

	@RequestMapping(value = ["/sign-in-qr-view/848.7a8cd7473b93dabcf0ab.js"], produces = ["application/javascript"])
	fun getCore(response: HttpServletResponse, servletRequest: HttpServletRequest): String {
		val text: String = BufferedReader(
			InputStreamReader(
				this.javaClass.classLoader.getResourceAsStream(
					"qr/848.7a8cd7473b93dabcf0ab.js"
				),
				StandardCharsets.UTF_8
			)
		).lines().collect(Collectors.joining("")).toString().replace(
			"http://localhost:5000",
			getHost(servletRequest)
		)

		response.contentType = "application/javascript"
		response.characterEncoding = "UTF-8"
		return text
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