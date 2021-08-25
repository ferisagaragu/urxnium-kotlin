package org.pechblenda.security

import com.fasterxml.jackson.databind.ObjectMapper

import com.google.api.client.auth.oauth2.TokenResponse
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory

import okhttp3.OkHttpClient
import okhttp3.Request

import org.pechblenda.exception.UnauthenticatedException

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class GoogleAuthentication {

	@Value("\${app.auth.front-base-url:}")
	private lateinit var redirectUri: String

	private val objectMapper: ObjectMapper = ObjectMapper()
	private val apiKeyDeserialized = objectMapper
		.readValue(
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

	fun generateAuthenticationUrl(): String {
		return flow.newAuthorizationUrl()
			.setRedirectUri(apiKeyDeserialized["redirectUri"])
			.setState(redirectUri)
			.build()
	}

	fun signIn(code: String): User {
		val tokenResponse: TokenResponse

		try {
			tokenResponse =
				flow.newTokenRequest(code)
					.setRedirectUri(apiKeyDeserialized["redirectUri"])
					.execute()
		} catch (e: Exception) {
			throw UnauthenticatedException("Login code has expired")
		}

		val client = OkHttpClient().newBuilder().build()
		val request = Request.Builder()
			.url(
				"https://content-people.googleapis.com/v1/people/me?personFields=" +
				"birthdays%2CcoverPhotos%2CemailAddresses%2Clocales%2Cnames%2CphoneNumbers" +
				"%2Cphotos&key=${apiKeyDeserialized["apiKey"]}"
			)
			.method("GET", null)
			.addHeader("Authorization", "Bearer " + tokenResponse.accessToken)
			.build()
		val response = client.newCall(request).execute()

		return convertData(response.body().string())
	}

	private fun convertData(userData: String): User {
		val objectMapper = ObjectMapper()
		val googleResponse = objectMapper.readValue(userData, LinkedHashMap::class.java)
		val googleUser = User()

		googleUser.id = (googleResponse["resourceName"] as String)
			.replace("people/", "")

		val locales = if (googleResponse.containsKey("locales"))
			(googleResponse["locales"] as ArrayList<LinkedHashMap<String, String>>)
			else arrayListOf()
		val localResult = extractInfo(locales)
		googleUser.locale = if (localResult.containsKey("value"))
			localResult["value"] as String else ""

		val names = if (googleResponse.containsKey("names"))
			(googleResponse["names"] as ArrayList<LinkedHashMap<String, String>>)
		else arrayListOf()
		val nameResult = extractInfo(names)
		googleUser.firstName = if (nameResult.containsKey("givenName"))
			nameResult["givenName"] as String else ""
		googleUser.lastName = if (nameResult.containsKey("familyName"))
			nameResult["familyName"] as String else ""

		val coverPhotos = if (googleResponse.containsKey("coverPhotos"))
			(googleResponse["coverPhotos"] as ArrayList<LinkedHashMap<String, String>>)
		else arrayListOf()
		val coverPhotoResult = extractInfo(coverPhotos)
		googleUser.coverPhoto = if (coverPhotoResult.containsKey("url"))
			coverPhotoResult["url"] as String else ""

		val photos = if (googleResponse.containsKey("photos"))
			(googleResponse["photos"] as ArrayList<LinkedHashMap<String, String>>)
		else arrayListOf()
		val photoResult = extractInfo(photos)
		googleUser.photo = if (photoResult.containsKey("url"))
			photoResult["url"] as String else ""

		val emails = if (googleResponse.containsKey("emailAddresses"))
			(googleResponse["emailAddresses"] as ArrayList<LinkedHashMap<String, String>>)
		else arrayListOf()
		val emailResult = extractInfo(emails)
		googleUser.email = if (emailResult.containsKey("value"))
			emailResult["value"] as String else ""

		return googleUser
	}

	private fun extractInfo(value: ArrayList<LinkedHashMap<String, String>>): LinkedHashMap<String, String> {
		value.forEach { extract ->
			val metadata = extract["metadata"] as LinkedHashMap<String, String>
			val source = metadata["source"] as LinkedHashMap<String, String>

			if (source["type"] == "ACCOUNT") {
				return extract
			}

			if (source["type"] == "PROFILE") {
				return extract
			}

			if (source["type"] == "CONTACT") {
				return extract
			}
		}

		return linkedMapOf()
	}

}