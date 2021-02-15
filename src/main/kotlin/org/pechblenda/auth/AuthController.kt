package org.pechblenda.auth

import org.pechblenda.auth.service.AuthService
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
	fun validateToken(): ResponseEntity<Any> {
		return try {
			authService.validateToken()
		} catch (e: ResponseStatusException) {
			httpExceptionResponse.error(e)
		}
	}

	@GetMapping("/can-activate-account/{userUid}")
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
	fun canChangePassword(
		@PathVariable("activatePassword") activatePassword: UUID
	): ResponseEntity<Any> {
		return try {
			authService.canChangePassword(activatePassword)
		} catch (e: ResponseStatusException) {
			httpExceptionResponse.error(e)
		}
	}

	@PostMapping("/activate-account")
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
	fun signUp(
		@RequestBody request: Request
	): ResponseEntity<Any> {
		return try {
			authService.signUp(request)
		} catch (e: ResponseStatusException) {
			httpExceptionResponse.error(e)
		}
	}

	@PostMapping("/sign-in")
	fun signIn(
		@RequestBody request: Request
	): ResponseEntity<Any> {
		return try {
			authService.signIn(request)
		} catch (e: ResponseStatusException) {
			httpExceptionResponse.error(e)
		}
	}

	@PostMapping("/refresh-token")
	fun refreshToken(
		@RequestBody request: Request
	): ResponseEntity<Any> {
		return try {
			authService.refreshToken(request)
		} catch (e: ResponseStatusException) {
			httpExceptionResponse.error(e)
		}
	}

	@GetMapping("/profile/{lyrics}/{color}/{background}")
	fun generateProfileImage(
		@PathVariable("lyrics") lyrics: String,
		@PathVariable("color") color: String,
		@PathVariable("background") background: String
	): ResponseEntity<Any> {
		return authService.generateProfileImage(lyrics, color, background)
	}

}