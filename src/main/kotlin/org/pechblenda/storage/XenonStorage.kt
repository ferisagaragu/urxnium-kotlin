package org.pechblenda.storage

import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.MediaType

import com.fasterxml.jackson.databind.ObjectMapper

import java.io.InputStream

import org.springframework.beans.factory.annotation.Value

import kotlin.collections.LinkedHashMap

class XenonStorage {

	@Value("\${storage.xenon.base-url:}")
	private lateinit var storageUrl: String

	@Value("\${storage.xenon.project-uuid:}")
	private lateinit var projectUuid: String

	fun put(
		mimeType: String,
		name: String,
		extension: String,
		file: InputStream
	): FileInfo {
		if (storageUrl.isBlank()) {
			throw Exception("Xenon storage base url not found")
		}

		if (projectUuid.isBlank()) {
			throw Exception("Xenon storage project identifier not found")
		}

		val objectMapper = ObjectMapper()
		val client = OkHttpClient().newBuilder()
			.build()
		val body: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
			.addFormDataPart("projectUid", projectUuid)
			.addFormDataPart(
				"file",
				"$name$extension",
				RequestBody.create(
					MediaType.parse(mimeType),
					file.readAllBytes()
				)
			)
			.build()
		val request = Request.Builder()
			.url("$storageUrl/storages")
			.method("POST", body)
			.build()
		val response = client.newCall(request).execute()
		val resp = objectMapper.readValue(
			response.body().string(),
			LinkedHashMap::class.java
		)["data"] as LinkedHashMap<String, String>

		return FileInfo(
			fileName = resp["originalName"]!!,
			refName = resp["name"]!!,
			mediaType = resp["mediaType"]!!,
			createDate = resp["createDate"]!!,
			url = "$storageUrl/storages/${resp["uid"]}"
		)
	}

}