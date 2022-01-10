package org.pechblenda.document

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory

import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

import org.pechblenda.exception.UnauthenticatedException
import org.pechblenda.document.entity.Cell

import org.springframework.stereotype.Component
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.util.UriUtils

import com.fasterxml.jackson.databind.ObjectMapper

@Component
class GoogleSheet {

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

	fun generateAuthenticationSheetUrl(): String {
		return flow.newAuthorizationUrl()
			.setRedirectUri(apiKeyDeserialized["redirectUri"])
			.setState("https://localhost")
			.setScopes(listOf(
				"https://www.googleapis.com/auth/drive",
				"https://www.googleapis.com/auth/drive.file",
				"https://www.googleapis.com/auth/spreadsheets"
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

	fun findCell(token: String, sheetCode: String, range: String): List<Cell> {
		val client = OkHttpClient().newBuilder().build()
		val request = Request.Builder()
			.url(
				"https://sheets.googleapis.com/v4/spreadsheets/" +
				"${sheetCode}/values/${UriUtils.encode(range, "UTF-8")}"
			)
			.method("GET", null)
			.addHeader("Authorization", "Bearer $token")
			.build()
		val response = client.newCall(request).execute()
		val data = objectMapper.readValue(response.body().string(), LinkedHashMap::class.java)
		var out = listOf<Cell>()

		try {
			out = (data["values"] as MutableList<List<Cell>>).map {
				value -> Cell(value[0])
			}
		} catch (e: Exception) { }

		return out
	}

	fun updateCell(token: String, sheetCode: String, range: String, cells: List<Cell>): Number {
		var cellsData = cells.map { cell -> "[\"${cell.value}\"]" }
		val data = "{\"values\":${cellsData}}"

		val body = RequestBody.create(
			MediaType.parse("application/json; charset=utf-8"),
			data
		)

		val client = OkHttpClient().newBuilder().build()
		val request = Request.Builder()
			.url(
				"https://content-sheets.googleapis.com/v4/spreadsheets/" +
				"${sheetCode}/values/${UriUtils.encode(range, "UTF-8")}?valueInputOption=RAW"
			)
			.method("PUT", body)
			.addHeader("Authorization", "Bearer $token")
			.build()
		val response = client.newCall(request).execute()

		return response.code()
	}

}