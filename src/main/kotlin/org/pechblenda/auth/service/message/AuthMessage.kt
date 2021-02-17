package org.pechblenda.auth.service.message

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class AuthMessage {

	@Value("\${app.language:en}")
	private lateinit var language: String

	@Value("\${message.auth.user-not-fount:}")
	private lateinit var userNotFount: String

	@Value("\${message.auth.account-not-active:}")
	private lateinit var accountNotActive: String

	@Value("\${message.auth.account-blocked:}")
	private lateinit var accountBlocked: String

	@Value("\${message.auth.activate-user-not-fount:}")
	private lateinit var activateUserNotFount: String

	@Value("\${message.auth.activate-user-invalid:}")
	private lateinit var activateUserInvalid: String

	fun getUserNotFount(): String {
		return when {
			userNotFount.isNotEmpty() -> userNotFount
			userNotFount.isEmpty() && language == "es" -> "Upss no se encuentra el usuario"
			else -> "Upss the user cannot be found"
		}
	}

	fun getAccountNotActive(): String {
		return when {
			accountNotActive.isNotEmpty() -> accountNotActive
			accountNotActive.isEmpty() && language == "es" ->
				"Upss tu cuenta no esta activada aun, sigue las instrucciones enviadas a tu correo electrónico para activarla"
			else -> "Upss your account is not activated yet, follow the instructions sent to your email to activate it"
		}
	}

	fun getAccountBlocked(): String {
		return when {
			accountBlocked.isNotEmpty() -> accountBlocked
			accountBlocked.isEmpty() && language == "es" ->
				"Upss tu cuenta se encuentra bloqueada, te enviamos a tu correo electrónico las razones"
			else -> "Upss your account is blocked, we will send you the reasons to your email"
		}
	}

	fun getActivateUserNotFount(): String {
		return when {
			activateUserNotFount.isNotEmpty() -> activateUserNotFount
			activateUserNotFount.isEmpty() && language == "es" ->
				"Upss no se encuentra el usuario que quieres activar"
			else -> "Upss the user you want to activate cannot be found"
		}
	}

}