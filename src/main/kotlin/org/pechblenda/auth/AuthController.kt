package org.pechblenda.auth

import org.pechblenda.auth.service.AuthService
import org.pechblenda.doc.annotation.ApiDocumentation
import org.pechblenda.exception.HttpExceptionResponse
import org.pechblenda.service.Request

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

import java.util.UUID

import javax.servlet.http.HttpServletRequest

@CrossOrigin(methods = [
	RequestMethod.GET,
	RequestMethod.POST
])
@RestController
@RequestMapping(name = "Auth", value = ["/api/auth"])
class AuthController {

	@Autowired
	private lateinit var authService: AuthService

	@Autowired
	private lateinit var httpExceptionResponse: HttpExceptionResponse

	@GetMapping("/validate-token")
	@ApiDocumentation(path = "api/assets/auth/validate-token.json")
	fun validateToken(): ResponseEntity<Any> {
		return try {
			authService.validateToken()
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

}