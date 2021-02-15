package org.pechblenda.auth.service.`interface`

import java.util.UUID

import org.pechblenda.service.Request

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable

interface IAuthService {
	fun validateToken(): ResponseEntity<Any>
	fun canActivate(userUid: UUID): ResponseEntity<Any>
	fun canChangePassword(activatePassword: UUID): ResponseEntity<Any>
	fun activateAccount(request: Request): ResponseEntity<Any>
	fun changePassword(request: Request): ResponseEntity<Any>
	fun recoverPassword(request: Request): ResponseEntity<Any>
	fun signUp(request: Request): ResponseEntity<Any>
	fun signIn(request: Request): ResponseEntity<Any>
	fun refreshToken(request: Request): ResponseEntity<Any>
	fun generateProfileImage(lyrics: String, color: String, background: String): ResponseEntity<Any>
}