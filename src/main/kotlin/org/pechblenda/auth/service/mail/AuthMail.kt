package org.pechblenda.auth.service.mail

import org.pechblenda.auth.entity.IUser
import org.pechblenda.mail.GoogleMail
import org.pechblenda.mail.TemplateActionMail

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class AuthMail {

	@Autowired
	private lateinit var googleMail: GoogleMail

	@Value("\${app.language:en}")
	private lateinit var language: String

	@Value("\${mail.auth.activate-account-subject:}")
	private lateinit var activateAccountSubject: String

	@Value("\${mail.auth.activate-account-title:}")
	private lateinit var activateAccountTitle: String

	@Value("\${mail.auth.activate-account-body:}")
	private lateinit var activateAccountBody: String

	@Value("\${mail.auth.activate-account-action:}")
	private lateinit var activateAccountAction: String

	@Value("\${mail.auth.activate-account-action-link:}")
	private lateinit var activateAccountActionLink: String

	@Value("\${mail.auth.recover-password-subject:}")
	private lateinit var recoverPasswordSubject: String

	@Value("\${mail.auth.recover-password-title:}")
	private lateinit var recoverPasswordTitle: String

	@Value("\${mail.auth.recover-password-body:}")
	private lateinit var recoverPasswordBody: String

	@Value("\${mail.auth.recover-password-action:}")
	private lateinit var recoverPasswordAction: String

	@Value("\${mail.auth.recover-password-action-link:}")
	private lateinit var recoverPasswordActionLink: String

	fun sendActivateAccountMail(user: IUser) {
		googleMail.send(
			when {
				activateAccountSubject.isNotEmpty() -> activateAccountSubject
				(activateAccountSubject.isEmpty() && language == "es") -> "Verificación de cuenta"
				else -> "Verify Account"
			},
			TemplateActionMail(
				title = when {
					activateAccountTitle.isNotEmpty() -> activateAccountTitle
					(activateAccountTitle.isEmpty() && language == "es") -> "Verificación de cuenta"
					else -> "Verify Account"
				},
				body = when {
					activateAccountBody.isNotEmpty() -> activateAccountBody
					(activateAccountBody.isEmpty() && language == "es") -> ("Gracias por registrarte {name}, " +
						"antes de comenzar nos gustaría verificar tu identidad." +
						"<br>Ingresa al sistema para activar tu cuenta.").replace("{name}", user.name)
					else -> ("Thank you for registering {name}, before we begin we would like to verify your " +
						"identity. <br> Login to the system to activate your account.").replace("{name}", user.name)
				},
				actionLabel = when {
					activateAccountAction.isNotEmpty() -> activateAccountAction
					(activateAccountAction.isEmpty() && language == "es") -> "ACTIVAR MI CUENTA"
					else -> "ACTIVATE MY ACCOUNT"
				},
				actionLink =  when {
					activateAccountActionLink.isNotEmpty() -> "${activateAccountActionLink}/${user.uuid}"
					else -> "http://localhost:8080/${user.uuid}"
				}
			),
			user.email
		)
	}

	fun sendRecoverPasswordMail(user: IUser) {
		googleMail.send(
			when {
				recoverPasswordSubject.isNotEmpty() -> recoverPasswordSubject
				(recoverPasswordSubject.isEmpty() && language == "es") -> "Recupera tu contraseña"
				else -> "Recover your password"
			},
			TemplateActionMail(
				title = when {
					recoverPasswordTitle.isNotEmpty() -> recoverPasswordTitle
					(recoverPasswordTitle.isEmpty() && language == "es") ->
						"Lamentamos que hayas olvidado tus credenciales de acceso {name}".replace("{name}", user.name)
					else -> "We are sorry that you forgot your login credentials {name}".replace("{name}", user.name)
				},
				body = when {
					recoverPasswordBody.isNotEmpty() -> recoverPasswordBody
					(recoverPasswordBody.isEmpty() && language == "es") ->
						"Ingresa a la siguiente dirección para recuperar tu contraseña."
					else -> "Enter the following address to recover your password."
				},
				actionLabel = when {
					recoverPasswordAction.isNotEmpty() -> recoverPasswordAction
					(recoverPasswordAction.isEmpty() && language == "es") -> "RECUPERAR MI CONTRASEÑA"
					else -> "RECOVER MY PASSWORD"
				},
				actionLink =  when {
					recoverPasswordActionLink.isNotEmpty() -> "${recoverPasswordActionLink}/${user.activatePassword}"
					else -> "http://localhost:8080/${user.activatePassword}"
				}
			),
			user.email
		)
	}

}