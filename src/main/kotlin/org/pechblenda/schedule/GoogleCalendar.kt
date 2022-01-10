package org.pechblenda.schedule

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory

import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

import org.pechblenda.exception.UnauthenticatedException
import org.pechblenda.schedule.entity.Event

import org.springframework.stereotype.Component
import org.springframework.beans.factory.annotation.Value

import com.fasterxml.jackson.databind.ObjectMapper

@Component
class GoogleCalendar {

	@Value("\${app.auth.front-base-url:}")
	private lateinit var redirectUri: String

	private val objectMapper: ObjectMapper = ObjectMapper()
	private val apiKeyDeserialized = objectMapper.readValue(
		this
			.javaClass
			.classLoader
			.getResourceAsStream("googleApiKey.json"),
		LinkedHashMap::class.java
	) as LinkedHashMap<String, String>

	private val flow = GoogleAuthorizationCodeFlow.Builder(
		NetHttpTransport(),
		JacksonFactory(),
		apiKeyDeserialized["clientId"],
		apiKeyDeserialized["clientSecret"],
		listOf("email", "profile")
	).build()

	fun generateAuthenticationCalendarUrl(): String {
		return flow.newAuthorizationUrl()
			.setRedirectUri(apiKeyDeserialized["redirectUri"])
			.setState(redirectUri)
			.setScopes(listOf(
				"https://www.googleapis.com/auth/calendar",
				"https://www.googleapis.com/auth/calendar.events"
			))
			.build()
	}

	fun getAuthTokenFromCode(code: String): String {
		try {
			return flow.newTokenRequest(code)
				.setRedirectUri(apiKeyDeserialized["redirectUri"])
				.execute()
				.accessToken
		} catch (e: Exception) {
			throw UnauthenticatedException("Login code has expired")
		}
	}

	fun createEvent(token: String, event: Event): Number {
		val body = RequestBody.create(
			MediaType.parse("application/json; charset=utf-8"),
			objectMapper.writeValueAsString(event)
		)

		val client = OkHttpClient().newBuilder().build()
		val request = Request.Builder()
			.url("https://www.googleapis.com/calendar/v3/calendars/primary/events")
			.method("POST", body)
			.addHeader("Authorization", "Bearer $token")
			.build()
		val response = client.newCall(request).execute()

		return response.code()
	}

}