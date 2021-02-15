package org.pechblenda.storage

import com.fasterxml.jackson.databind.ObjectMapper

import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.MediaType

import java.io.InputStream

import kotlin.collections.LinkedHashMap

class XenonStorage(
	val storageUrl: String
) {

	fun put(
		mimeType: String,
		name: String,
		extension: String,
		file: InputStream
	): FileInfo {
		val objectMapper = ObjectMapper()
		val client = OkHttpClient().newBuilder()
			.build()
		val body: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
			.addFormDataPart("projectUid", "6ea35904-4af3-4e38-8713-7a52a97df3bd")
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
			.url(storageUrl)
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
			url = "$storageUrl/${resp["uid"]}"
		)
	}

}