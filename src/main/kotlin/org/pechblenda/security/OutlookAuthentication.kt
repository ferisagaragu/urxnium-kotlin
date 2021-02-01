package org.pechblenda.security

import com.fasterxml.jackson.databind.ObjectMapper

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.MultipartBody

import java.net.URLEncoder

class OutlookAuthentication(
	val redirectUri: String
) {

	private val objectMapper: ObjectMapper = ObjectMapper()
	private val apiKeyDeserialized = objectMapper
		.readValue(
			this
				.javaClass
				.classLoader
				.getResourceAsStream("outlookApiKey.json"),
			LinkedHashMap::class.java
		) as LinkedHashMap<String, String>

	fun generateAuthenticationUrl(): String {
		return "https://login.microsoftonline.com/common/oauth2/v2.0/authorize" +
			"?client_id=" + apiKeyDeserialized["clientId"] +
			"&response_type=code" +
			"&redirect_uri=" + URLEncoder.encode(apiKeyDeserialized["redirectUri"], "UTF-8") +
			"&response_mode=query" +
			"&scope=https%3A%2F%2Fgraph.microsoft.com%2FUser.Read" +
			"&state=${URLEncoder.encode(redirectUri, "UTF-8")}"
	}

	fun signIn(code: String): OutlookUser {
		val token = getOutlookToken(code)
		val userInfo = getUserInfo(token)

		return OutlookUser(
			id = userInfo["id"]!!,
			firstName = userInfo["givenName"]!!,
			lastName = userInfo["surname"]!!,
			email = userInfo["userPrincipalName"]!!
		)
	}

	private fun getOutlookToken(code: String): String {
		val client = OkHttpClient().newBuilder().build()
		val body = MultipartBody.Builder().setType(MultipartBody.FORM)
			.addFormDataPart("grant_type", "authorization_code")
			.addFormDataPart("client_id", apiKeyDeserialized["clientId"])
			.addFormDataPart("scope", "https://graph.microsoft.com/User.Read")
			.addFormDataPart("code", code)
			.addFormDataPart("redirect_uri", apiKeyDeserialized["redirectUri"])
			.addFormDataPart("client_secret", apiKeyDeserialized["clientSecret"])
			.build()
		val request = Request.Builder()
			.url("https://login.microsoftonline.com/common/oauth2/v2.0/token")
			.method("POST", body)
			.build()
		val response = client.newCall(request).execute()

		return (objectMapper
			.readValue(response.body().string(), LinkedHashMap::class.java)
			as LinkedHashMap<String, String>)["access_token"]!!
	}

	private fun getUserInfo(token: String): LinkedHashMap<String, String> {
		val client = OkHttpClient().newBuilder().build()
		val request = Request.Builder()
			.url("https://graph.microsoft.com/v1.0/me")
			.method("GET", null)
			.addHeader(
				"Authorization",
				"Bearer $token"
			)
			.build()
		val response = client.newCall(request).execute()

		return objectMapper
			.readValue(response.body().string(), LinkedHashMap::class.java)
			as LinkedHashMap<String, String>
	}

}