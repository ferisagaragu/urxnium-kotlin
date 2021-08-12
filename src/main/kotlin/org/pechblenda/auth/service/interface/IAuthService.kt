package org.pechblenda.auth.service.`interface`

import java.util.UUID

import org.pechblenda.service.Request

import org.springframework.http.ResponseEntity

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

interface IAuthService {
	fun validateToken(authorization: String): ResponseEntity<Any>
	fun canActivate(userUid: UUID): ResponseEntity<Any>
	fun canChangePassword(activatePassword: UUID): ResponseEntity<Any>
	fun generateProfileImage(initialLetter: Char, color: String, background: String): ResponseEntity<Any>
	fun generateGoogleAuthenticationUrl(): ResponseEntity<Any>
	fun generateOutlookAuthenticationUrl(): ResponseEntity<Any>
	fun generateQRAuthentication(servletRequest: HttpServletRequest): ResponseEntity<Any>
	fun findBasicUserInfoByUuid(userUuid: UUID): ResponseEntity<Any>
	fun signInQRView(response: HttpServletResponse): ResponseEntity<Any>
	fun activateAccount(request: Request): ResponseEntity<Any>
	fun changePassword(request: Request): ResponseEntity<Any>
	fun recoverPassword(request: Request): ResponseEntity<Any>
	fun signUp(request: Request, servletRequest: HttpServletRequest): ResponseEntity<Any>
	fun signUpQR(request: Request, servletRequest: HttpServletRequest): ResponseEntity<Any>
	fun signIn(request: Request): ResponseEntity<Any>
	fun signInFormCode(request: Request, servletRequest: HttpServletRequest): ResponseEntity<Any>
	fun signInFormQR(request: Request): ResponseEntity<Any>
	fun refreshToken(request: Request): ResponseEntity<Any>
}