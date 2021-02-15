package org.pechblenda.mail

import org.springframework.beans.factory.annotation.Value

import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.Properties
import java.util.Date
import java.util.stream.Collectors

import javax.mail.Session
import javax.mail.Authenticator
import javax.mail.PasswordAuthentication
import javax.mail.Message
import javax.mail.Transport
import javax.mail.MessagingException
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class GoogleMail {

	@Value("\${app.name:Urxnium}")
	private lateinit var appName: String

	@Value("\${app.mail.username:javabrain.email@gmail.com}")
	private lateinit var user: String

	@Value("\${app.mail.password:sevjdmxtofenglzn}")
	private lateinit var password: String

	@Value("\${app.mail.color:#FFF}")
	private lateinit var color: String

	@Value("\${app.mail.background:#3F51B5}")
	private lateinit var background: String

	fun send(subject: String, content: String, vararg emails: String): Boolean {
		var out = true

		for (email: String in emails) {
			out = out && sendIndividual(email, subject, content)
		}

		return out
	}

	fun send(
		subject: String,
		templateActionMail: TemplateActionMail,
		vararg emails: String
	): Boolean {
		var out = true
		var text: String = BufferedReader(
			InputStreamReader(
				this.javaClass.classLoader.getResourceAsStream("mail-template.html"),
				StandardCharsets.UTF_8
			)
		).lines().collect(Collectors.joining("\n"))

		text = text.replace("\${appName}", appName)
		text = text.replace("\${title}", templateActionMail.title)
		text = text.replace("\${body}", templateActionMail.body)
		text = text.replace(
			"\${buttonCode}",
			"                              <!-- BUTTON -->\n" +
				"                              <tr>\n" +
				"                                <td align=\"left\" style=\"padding: 18px 18px 18px 18px; mso-alt-padding: 18px 18px 18px 18px!important;\">\n" +
				"                                  <table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n" +
				"                                    <tr>\n" +
				"                                      <td>\n" +
				"                                        <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n" +
				"                                          <tr>\n" +
				"                                            <td align=\"left\" style=\"border-radius: 3px;\" bgcolor=\"$background\">\n" +
				"                                              <a class=\"button raised\" href=\"\${actionLink}\" target=\"_blank\" style=\"font-size: 14px; line-height: 14px; font-weight: 500; font-family: Helvetica, Arial, sans-serif; color: $color; text-decoration: none; border-radius: 3px; padding: 10px 25px; border: 1px solid $background; display: inline-block;\">\${actionLabel}</a>\n" +
				"                                            </td>\n" +
				"                                          </tr>\n" +
				"                                        </table>\n" +
				"                                      </td>\n" +
				"                                    </tr>\n" +
				"                                  </table>\n" +
				"                                </td>\n" +
				"                              </tr>\n" +
				"                              <!-- END BUTTON -->"
		)
		text = text.replace("\${actionLabel}", templateActionMail.actionLabel)
		text = text.replace("\${actionLink}", templateActionMail.actionLink)
		text = text.replace("\${color}", color)
		text = text.replace("\${primaryColor}", background)

		for (email: String in emails) {
			out = out && sendIndividual(email, subject, text)
		}

		return out
	}

	fun send(
		subject: String,
		templateMail: TemplateMail,
		vararg emails: String
	): Boolean {
		var out = true
		var text: String = BufferedReader(
			InputStreamReader(
				this.javaClass.classLoader.getResourceAsStream("mail-template.html"),
				StandardCharsets.UTF_8
			)
		).lines().collect(Collectors.joining("\n"))

		text = text.replace("\${appName}", appName)
		text = text.replace("\${title}", templateMail.title)
		text = text.replace(
			"\${body}",
			templateMail.body +
				"<div style=\"margin-bottom: 18px !important;\"></div>"
		)
		text = text.replace("\${color}", color)
		text = text.replace("\${primaryColor}", background)
		text = text.replace("\${buttonCode}", "")

		for (email: String in emails) {
			out = out && sendIndividual(email, subject, text)
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
			msg.setFrom(InternetAddress("noreply@gmail.com", false))
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
