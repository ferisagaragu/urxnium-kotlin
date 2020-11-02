package org.pechblenda.mail

import java.util.*
import javax.mail.*

import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


class GoogleMail(
	private val user: String,
	private val password: String
) {

	fun send(subject: String, content: String, vararg emails: String): Boolean {
		var out = true

		for (email: String in emails) {
			out = out && sendIndividual(email, subject, content)
		}

		return out
	}


	private fun sendIndividual(
		email: String,
		subject: String,
		content: String
	): Boolean {
		try {
			val props = Properties()
			props["mail.smtp.auth"] = "true"
			props["mail.smtp.starttls.enable"] = "true"
			props["mail.smtp.host"] = "smtp.gmail.com"
			props["mail.smtp.port"] = "587"

			val session: Session = Session.getInstance(
				props,
				object : Authenticator() {
					override fun getPasswordAuthentication(): PasswordAuthentication {
						return PasswordAuthentication(user, password)
					}
				}
			)

			val msg: Message = MimeMessage(session)
			msg.setFrom(InternetAddress("javabrain.email@gmail.com", false))
			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email))
			msg.setContent(content, "text/html")
			msg.subject = subject
			msg.sentDate = Date()
			Transport.send(msg)
		} catch (e: MessagingException) {
			return false
		}

		return true
	}

}
