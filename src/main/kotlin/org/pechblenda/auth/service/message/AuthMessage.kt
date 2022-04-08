package org.pechblenda.auth.service.message

import org.pechblenda.auth.enums.AccountType
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class AuthMessage {

	@Value("\${app.language:en}")
	private lateinit var language: String

	@Value("\${app.formal.language:}")
	private lateinit var formalLanguage: String

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

	@Value("\${message.auth.recover-code-invalid:}")
	private lateinit var recoverCodeInvalid: String

	@Value("\${message.auth.password-required:}")
	private lateinit var passwordRequired: String

	@Value("\${message.auth.account-be-activated:}")
	private lateinit var accountBeActivated: String

	@Value("\${message.auth.account-activated:}")
	private lateinit var accountActivated: String

	@Value("\${message.auth.account-not-match:}")
	private lateinit var accountNotMatch: String

	@Value("\${message.auth.account-not-activate:}")
	private lateinit var accountNotActivate: String

	@Value("\${message.auth.password-changed:}")
	private lateinit var passwordChanged: String

	@Value("\${message.auth.email-required:}")
	private lateinit var emailRequired: String

	@Value("\${message.auth.recover-instruction:}")
	private lateinit var recoverInstruction: String

	@Value("\${message.auth.name-required:}")
	private lateinit var nameRequired: String

	@Value("\${message.auth.surname-required:}")
	private lateinit var surnameRequired: String

	@Value("\${message.auth.mother-surname-required:}")
	private lateinit var motherSurnameRequired: String

	@Value("\${message.auth.user-name-required:}")
	private lateinit var userNameRequired: String

	@Value("\${message.auth.user-name-registered:}")
	private lateinit var userNameRegistered: String

	@Value("\${message.auth.email-registered:}")
	private lateinit var emailRegistered: String

	@Value("\${message.auth.account-created:}")
	private lateinit var accountCreated: String

	@Value("\${message.auth.password-incorrect:}")
	private lateinit var passwordIncorrect: String

	@Value("\${message.auth.refresh-token-required:}")
	private lateinit var refreshTokenRequired: String

	@Value("\${message.auth.access-code-required:}")
	private lateinit var accessCodeRequired: String

	@Value("\${message.auth.type-code-required:}")
	private lateinit var typeCodeRequired: String

	@Value("\${message.auth.account-type-not-recover:}")
	private lateinit var accountTypeNotRecover: String

	@Value("\${message.auth.account-type-not-valid:}")
	private lateinit var accountTypeNotValid: String

	@Value("\${message.auth.account-registered:}")
	private lateinit var accountRegistered: String

	@Value("\${message.auth.activate-qr-user-secret-required:}")
	private lateinit var activateQRUserSecretRequired: String

	@Value("\${message.auth.activate-qr-user-verify-code-required:}")
	private lateinit var activateQRUserVerifyCodeRequired: String

	@Value("\${message.auth.activate-qr-user-verify-code-invalid:}")
	private lateinit var activateQRUserVerifyCodeInvalid: String

	@Value("\${message.auth.sign-up-qr-uuid-required:}")
	private lateinit var signUpQRUuidRequired: String

	@Value("\${message.auth.sign-up-qr-code-registered:}")
	private lateinit var signUpQRCodeRegistered: String

	@Value("\${message.auth.sign-up-qr-code-invalid:}")
	private lateinit var signUpQRCodeInvalid: String

	@Value("\${message.auth.unauthenticated:}")
	private lateinit var unauthenticated: String

	fun getUserNotFount(): String {
		return when {
			userNotFount.isNotEmpty() -> userNotFount
			userNotFount.isEmpty() && language == "es" ->
				"${if (formalLanguage != "true") "Upss no" else "No"} se encuentra el usuario"
			else -> "${if (formalLanguage != "true") "Upss the" else "The"} user cannot be found"
		}
	}

	fun getAccountNotActive(): String {
		return when {
			accountNotActive.isNotEmpty() -> accountNotActive
			accountNotActive.isEmpty() && language == "es" ->
				"${if (formalLanguage != "true") "Upss tu" else "Tu"} cuenta no esta activada aun, sigue las " +
						"instrucciones enviadas a tu correo electrónico para activarla"
			else -> "${if (formalLanguage != "true") "Upss your" else "Your"} account is not activated yet, follow " +
					"the instructions sent to your email to activate it"
		}
	}

	fun getAccountBlocked(): String {
		return when {
			accountBlocked.isNotEmpty() -> accountBlocked
			accountBlocked.isEmpty() && language == "es" ->
				"${if (formalLanguage != "true") "Upss tu" else "Tu"} cuenta se encuentra bloqueada, " +
						"te enviamos a tu correo electrónico las razones"
			else -> "${if (formalLanguage != "true") "Upss your" else "Your"} account is blocked, we will send " +
					"you the reasons to your email"
		}
	}

	fun getActivateUserNotFount(): String {
		return when {
			activateUserNotFount.isNotEmpty() -> activateUserNotFount
			activateUserNotFount.isEmpty() && language == "es" ->
				"${if (formalLanguage != "true") "Upss no" else "No"} se encuentra el usuario que quieres activar"
			else -> "${if (formalLanguage != "true") "Upss the" else "The"} user you want to activate cannot be found"
		}
	}

	fun getActivateUserInvalid(): String {
		return when {
			activateUserInvalid.isNotEmpty() -> activateUserInvalid
			activateUserInvalid.isEmpty() && language == "es" ->
				"${if (formalLanguage != "true") "Oops tu" else "Tu"} cuenta ya esta activada, intenta iniciar " +
						"sesión de forma habitual"
			else -> "${if (formalLanguage != "true") "Oops your" else "Your"} account is already activated, " +
					"try to log in regularly"
		}
	}

	fun getRecoverCodeInvalid(): String {
		return when {
			recoverCodeInvalid.isNotEmpty() -> recoverCodeInvalid
			recoverCodeInvalid.isEmpty() && language == "es" ->
				"${if (formalLanguage != "true") "Oops el" else "El"} código de recuperación no es valido"
			else -> "${if (formalLanguage != "true") "Oops the" else "The"} recovery code is not valid"
		}
	}

	fun getPasswordRequired(): String {
		return when {
			passwordRequired.isNotEmpty() -> passwordRequired
			passwordRequired.isEmpty() && language == "es" ->
				"${if (formalLanguage != "true") "Oops la" else "La"} contraseña es requerida"
			else -> "${if (formalLanguage != "true") "Oops password" else "Password"} is required"
		}
	}

	fun getAccountBeActivated(): String {
		return when {
			accountBeActivated.isNotEmpty() -> accountBeActivated
			accountBeActivated.isEmpty() && language == "es" ->
				"${if (formalLanguage != "true") "Oops tu" else "Tu"} cuenta ya esta activada"
			else -> "${if (formalLanguage != "true") "Oops your" else "Your"} account is already activated"
		}
	}

	fun getAccountActivated(): String {
		return when {
			accountActivated.isNotEmpty() -> accountActivated
			accountActivated.isEmpty() && language == "es" -> "Tu cuenta ha sido activada con éxito"
			else -> "Your account has been activated successfully"
		}
	}

	fun getAccountNotMatch(): String {
		return when {
			accountNotMatch.isNotEmpty() -> accountNotMatch
			accountNotMatch.isEmpty() && language == "es" ->
				"${if (formalLanguage != "true") "Upss no" else "No"} se encuentra ningún registro que coincida con el " +
						"correo electrónico o nombre de usuario"
			else -> "${if (formalLanguage != "true") "Upss no" else "No"} record is found that matches email or username"
		}
	}

	fun getAccountNotActivate(): String {
		return when {
			accountNotActivate.isNotEmpty() -> accountNotActivate
			accountNotActivate.isEmpty() && language == "es" ->
				"${if (formalLanguage != "true") "Oops tu" else "Tu"} cuenta aun no esta activada, revisa tu correo " +
						"electrónico para saber como activarla"
			else -> "${if (formalLanguage != "true") "Oops your" else "Your"} account is not activated yet, check your " +
					"email to know how to activate it"
		}
	}

	fun getPasswordChanged(): String {
		return when {
			passwordChanged.isNotEmpty() -> passwordChanged
			passwordChanged.isEmpty() && language == "es" -> "Has cambiado tu contraseña con éxito"
			else -> "You have successfully changed your password"
		}
	}

	fun getEmailRequired(): String {
		return when {
			emailRequired.isNotEmpty() -> emailRequired
			emailRequired.isEmpty() && language == "es" ->
				"${if (formalLanguage != "true") "Oops el" else "El"} correo electrónico es requerido"
			else -> "${if (formalLanguage != "true") "Oops email" else "Email"} is required"
		}
	}

	fun getRecoverInstruction(): String {
		return when {
			recoverInstruction.isNotEmpty() -> recoverInstruction
			recoverInstruction.isEmpty() && language == "es" ->
				"Hemos enviado un correo electrónico a {email} con las instrucciones para recuperar tu contraseña"
			else -> "We have sent an email to {email} with the instructions to recover your password"
		}
	}

	fun getNameRequired(): String {
		return when {
			nameRequired.isNotEmpty() -> nameRequired
			nameRequired.isEmpty() && language == "es" ->
				"${if (formalLanguage != "true") "Oops el" else "El"}  nombre es requerido"
			else -> "${if (formalLanguage != "true") "Oops name" else "Name"} is required"
		}
	}

	fun getSurnameRequired(): String {
		return when {
			surnameRequired.isNotEmpty() -> surnameRequired
			surnameRequired.isEmpty() && language == "es" ->
				"${if (formalLanguage != "true") "Oops el" else "El"} apellido paterno es requerido"
			else -> "${if (formalLanguage != "true") "Oops surname" else "Surname"} is required"
		}
	}

	fun getMotherSurnameRequired(): String {
		return when {
			motherSurnameRequired.isNotEmpty() -> motherSurnameRequired
			motherSurnameRequired.isEmpty() && language == "es" ->
				"${if (formalLanguage != "true") "Oops el" else "El"} apellido materno es requerido"
			else -> "${if (formalLanguage != "true") "Oops mother surname" else "Mother surname"} is required"
		}
	}

	fun getUserNameRequired(): String {
		return when {
			userNameRequired.isNotEmpty() -> userNameRequired
			userNameRequired.isEmpty() && language == "es" ->
				"${if (formalLanguage != "true") "Oops el" else "El"} nombre de usuario es requerido"
			else -> "${if (formalLanguage != "true") "Oops user name" else "User name"} is required"
		}
	}

	fun getUserNameRegistered(): String {
		return when {
			userNameRegistered.isNotEmpty() -> userNameRegistered
			userNameRegistered.isEmpty() && language == "es" -> "El nombre de usuario ya esta registrado"
			else -> "Username is already registered"
		}
	}

	fun getEmailRegistered(): String {
		return when {
			emailRegistered.isNotEmpty() -> emailRegistered
			emailRegistered.isEmpty() && language == "es" -> "El correo electrónico ya esta registrado registrado"
			else -> "The email is already registered registered"
		}
	}

	fun getAccountCreated(): String {
		return when {
			accountCreated.isNotEmpty() -> accountCreated
			accountCreated.isEmpty() && language == "es" -> "Tu cuenta ha sido creada con éxito, te enviamos " +
					"un correo electrónico con instrucciones de como activarla"
			else -> "Your account has been created successfully, we will send you an email with instructions on " +
					"how to activate it"
		}
	}

	fun getPasswordIncorrect(): String {
		return when {
			passwordIncorrect.isNotEmpty() -> passwordIncorrect
			passwordIncorrect.isEmpty() && language == "es" ->
				"${if (formalLanguage != "true") "Oops la" else "La"} contraseña es incorrecta"
			else -> "${if (formalLanguage != "true") "Oops the" else "The"} password is wrong"
		}
	}

	fun getRefreshTokenRequired(): String {
		return when {
			refreshTokenRequired.isNotEmpty() -> refreshTokenRequired
			refreshTokenRequired.isEmpty() && language == "es" ->
				"${if (formalLanguage != "true") "Oops el" else "El"} refresh Token es requerido"
			else -> "${if (formalLanguage != "true") "Oops the" else "The"} refresh Token is required"
		}
	}

	fun getAccessCodeRequired(): String {
		return when {
			accessCodeRequired.isNotEmpty() -> accessCodeRequired
			accessCodeRequired.isEmpty() && language == "es" ->
				"${if (formalLanguage != "true") "Oops el" else "El"} código de acceso es necesario para iniciar sesión"
			else -> "${if (formalLanguage != "true") "Oops the" else "The"} access code is required to log in"
		}
	}

	fun getTypeCodeRequired(): String {
		return when {
			typeCodeRequired.isNotEmpty() -> typeCodeRequired
			typeCodeRequired.isEmpty() && language == "es" ->
				"${if (formalLanguage != "true") "Oops el" else "El"} tipo de código es necesario, los tipos aceptados son " +
						"'Google' u 'Outlook'"
			else -> "${if (formalLanguage != "true") "Oops the" else "The"} type of code is necessary, the accepted types " +
					"are 'Google' or 'Outlook'"
		}
	}

	fun getAccountTypeNotValid(accountType: String): String {
		return when {
			accountTypeNotValid.isNotEmpty() -> accountTypeNotValid
			accountTypeNotValid.isEmpty() && language == "es" -> "${if (formalLanguage != "true") "Oops no" else "No"} " +
				"se puede iniciar sesión con esta cuenta por que ha sido registrada como una cuenta externa de " +
				"'${if (accountType == AccountType.GMAIL.name) "Google" else "Outlook"}', intenta iniciar " +
				"sesión por medio de el mismo proveedor"
			else -> "${if (formalLanguage != "true") "Oops cannot" else "Cannot"} log in with this account because it " +
					"has been registered as an external account of " +
					"'${if (accountType == AccountType.GMAIL.name) "Google" else "Outlook"}', " +
					"try log in through the same provider"
		}
	}

	fun getAccountTypeNotRecover(accountType: String): String {
		return when {
			accountTypeNotRecover.isNotEmpty() -> accountTypeNotRecover
			accountTypeNotRecover.isEmpty() && language == "es" -> "${if (formalLanguage != "true") "Oops no" else "No"} " +
				"se recuperar la contraseña de esta cuenta por que ha sido registrada como una cuenta externa de " +
				"'${if (accountType == AccountType.GMAIL.name) "Google" else "Outlook"}', intenta gestionar " +
				"la recuperación por medio de el mismo proveedor"
			else -> "${if (formalLanguage != "true") "Oops the" else "The"} password of this account will be recovered " +
					"because it has been registered as an external " +
					"'${if (accountType == AccountType.GMAIL.name) "Google" else "Outlook"}' account, try to manage the " +
					"recovery through the same provider"
		}
	}

	fun getAccountRegistered(): String {
		return when {
			accountRegistered.isNotEmpty() -> accountRegistered
			accountRegistered.isEmpty() && language == "es" ->
				"${if (formalLanguage != "true") "Oops ya" else "Ya"} hay una cuenta registrada con este correo " +
					"electrónico"
			else -> "${if (formalLanguage != "true") "Oops there" else "There"} is already an account registered " +
					"with this email"
		}
	}

	fun getActivateQRUserSecretRequired(): String {
		return when {
			activateQRUserSecretRequired.isNotEmpty() -> activateQRUserSecretRequired
			activateQRUserSecretRequired.isEmpty() && language == "es" ->
				"${if (formalLanguage != "true") "Oops el" else "El"} secreto es requerido"
			else -> "${if (formalLanguage != "true") "Oops the" else "The"} secret is required"
		}
	}

	fun getActivateQRUserVerifyCodeRequired(): String {
		return when {
			activateQRUserVerifyCodeRequired.isNotEmpty() -> activateQRUserVerifyCodeRequired
			activateQRUserVerifyCodeRequired.isEmpty() && language == "es" ->
				"${if (formalLanguage != "true") "Oops el" else "El"} código de verificación es requerido"
			else -> "${if (formalLanguage != "true") "Oops the" else "The"} verify code is required"
		}
	}

	fun getActivateQRUserVerifyCodeInvalid(): String {
		return when {
			activateQRUserVerifyCodeInvalid.isNotEmpty() -> activateQRUserVerifyCodeInvalid
			activateQRUserVerifyCodeInvalid.isEmpty() && language == "es" ->
				"${if (formalLanguage != "true") "Oops el" else "El"} código de activación es invalido"
			else -> "${if (formalLanguage != "true") "Oops the" else "The"} activation code is invalid"
		}
	}

	fun getSignUpQRUuidRequired(): String {
		return when {
			signUpQRUuidRequired.isNotEmpty() -> signUpQRUuidRequired
			signUpQRUuidRequired.isEmpty() && language == "es" ->
				"${if (formalLanguage != "true") "Oops el" else "El"} uuid del dispositivo es requerido"
			else -> "${if (formalLanguage != "true") "Oops the" else "The"} uuid device is invalid"
		}
	}

	fun getSignUpQRCodeRegistered(): String {
		return when {
			signUpQRCodeRegistered.isNotEmpty() -> signUpQRCodeRegistered
			signUpQRCodeRegistered.isEmpty() && language == "es" ->
				"${if (formalLanguage != "true") "Oops el" else "El"} código de inicio de sesión ya está registrado"
			else -> "${if (formalLanguage != "true") "Oops the" else "The"} login code is already registered"
		}
	}

	fun getSignUpQRCodeInvalid(): String {
		return when {
			signUpQRCodeInvalid.isNotEmpty() -> signUpQRCodeInvalid
			signUpQRCodeInvalid.isEmpty() && language == "es" ->
				"${if (formalLanguage != "true") "Oops el" else "El"} código que ingresaste no es valido"
			else -> "${if (formalLanguage != "true") "Oops the" else "The"} code you entered is not valid"
		}
	}

	fun getUnauthenticated(): String {
		return when {
			unauthenticated.isNotEmpty() -> unauthenticated
			unauthenticated.isEmpty() && language == "es" ->
				"${if (formalLanguage != "true") "Oops no" else "No"} estas autorizado para realizar esta acción"
			else -> "${if (formalLanguage != "true") "Oops you" else "You"} are not authorize to do this action"
		}
	}

}