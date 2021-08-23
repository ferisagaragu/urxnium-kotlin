package org.pechblenda.hook

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class SlackAlert {

	@Value("\${app.hook.slack-notification-url:}")
	lateinit var slackNotificationUrl: String

	fun send(slackMessage: SlackMessage) {
		send(slackNotificationUrl, slackMessage)
	}

	fun send(slackUrl: String, slackMessage: SlackMessage) {
		try {
			val restTemplate = RestTemplate()
			val data = mutableMapOf<String, Any>()
			data["username"] = slackMessage.userName
			data["text"] = slackMessage.text
			data["icon_url"] = slackMessage.iconUrl

			val request: HttpEntity<Map<String, Any>> = HttpEntity(data)
			restTemplate.exchange(
				slackUrl,
				HttpMethod.POST,
				request,
				String::class.java
			)
		} catch (e: Exception) {
			println(e.printStackTrace())
		}
	}

}