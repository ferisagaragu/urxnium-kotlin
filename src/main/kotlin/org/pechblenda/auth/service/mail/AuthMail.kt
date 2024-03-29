package org.pechblenda.auth.service.mail

import org.pechblenda.auth.entity.IUser
import org.pechblenda.core.shared.Server
import org.pechblenda.mail.GoogleMail
import org.pechblenda.mail.entity.TemplateActionMail
import org.pechblenda.mail.entity.TemplateMail

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import javax.servlet.http.HttpServletRequest

@Component
class AuthMail {

	@Autowired
	private lateinit var googleMail: GoogleMail

	@Autowired
	private lateinit var server: Server

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

	@Value("\${mail.auth.activate-qr-account-subject-qr:}")
	private lateinit var activateQRAccountSubject: String

	@Value("\${mail.auth.activate-qr-account-title:}")
	private lateinit var activateQRAccountTitle: String

	@Value("\${mail.auth.activate-qr-account-body-qr:}")
	private lateinit var activateQRAccountBody: String

	@Value("\${mail.auth.recover-qr-account-subject:}")
	private lateinit var recoverQRAccountSubject: String

	@Value("\${mail.auth.recover-qr-account-title:}")
	private lateinit var recoverQRAccountTitle: String

	@Value("\${mail.auth.recover-qr-account-body:}")
	private lateinit var recoverQRAccountBody: String

	@Value("\${mail.auth.recover-qr-account-action:}")
	private lateinit var recoverQRAccountAction: String

	@Value("\${mail.auth.recover-qr-account-action-link:}")
	private lateinit var recoverQRAccountActionLink: String

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

	fun sendActivateQRAccountMail(user: IUser) {
		googleMail.send(
			when {
				activateQRAccountSubject.isNotEmpty() -> activateQRAccountSubject
				(activateQRAccountSubject.isEmpty() && language == "es") -> "Verifica tu cuenta"
				else -> "Verify your account"
			},
			TemplateMail(
				title = when {
					activateQRAccountTitle.isNotEmpty() -> activateQRAccountTitle
					(activateQRAccountTitle.isEmpty() && language == "es") -> "Verificación de cuenta"
					else -> "Verify Account"
				},
				body = when {
					activateQRAccountBody.isNotEmpty() -> activateQRAccountBody
					(activateQRAccountBody.isEmpty() && language == "es") -> ("Gracias por registrarte {name}, " +
							"antes de comenzar nos gustaría verificar tu identidad." +
							"<br><br>Tu código de verificación es: <b>${user.password}</b>").replace("{name}", user.name)
					else -> ("Thank you for registering {name}, before we begin we would like to verify your identity." +
							"<br><br>Your verification code is: <b>${user.password}</b>").replace("{name}", user.name)
				}
			),
			user.email
		)
	}

	fun sendRecoverQRAccountMail(user: IUser, servletRequest: HttpServletRequest) {
		googleMail.send(
			when {
				recoverQRAccountSubject.isNotEmpty() -> recoverQRAccountSubject
				(recoverQRAccountSubject.isEmpty() && language == "es") -> "Recupera tu cuenta"
				else -> "Recover your account"
			},
			TemplateActionMail(
				title = when {
					recoverQRAccountTitle.isNotEmpty() -> recoverQRAccountTitle
					(recoverQRAccountTitle.isEmpty() && language == "es") ->
						"Ayudanos a identificar tu nuevo dispositivo {name}".replace("{name}", user.name)
					else -> "Help us identify your new device {name}".replace("{name}", user.name)
				},
				body = when {
					recoverQRAccountBody.isNotEmpty() -> recoverQRAccountBody
					(recoverQRAccountBody.isEmpty() && language == "es") ->
						"Ingresa a la siguiente dirección para recuperar tu cuenta."
					else -> "Enter the following address to recover your account."
				},
				actionLabel = when {
					recoverQRAccountAction.isNotEmpty() -> recoverQRAccountAction
					(recoverQRAccountAction.isEmpty() && language == "es") -> "RECUPERAR MI CUENTA"
					else -> "RECOVER MY ACCOUNT"
				},
				actionLink =  when {
					recoverQRAccountActionLink.isNotEmpty() -> "${recoverQRAccountActionLink}/${user.activatePassword}"
					else -> "${server.getHost(servletRequest)}" +
						"/rest/auth/sign-in-qr-view/#/device/recover/${user.activatePassword}"
				}
			),
			user.email
		)
	}

}