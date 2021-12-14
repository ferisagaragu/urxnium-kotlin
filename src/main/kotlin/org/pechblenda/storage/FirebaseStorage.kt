package org.pechblenda.storage

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.storage.Blob
import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions

import org.apache.commons.io.IOUtils
import java.io.InputStream
import java.util.Date
import java.util.UUID
import java.util.Base64
import javax.annotation.PostConstruct
import java.util.concurrent.TimeUnit

import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ClassPathResource

import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap
import org.pechblenda.storage.entity.FileInfo

class FirebaseStorage {

	@Value("\${storage.firebase.bucket:}")
	private lateinit var bucket: String

	@Value("\${storage.firebase.service-account-key-path:}")
	private lateinit var serviceAccountKeyPath: String

	private var storage: Storage? = null

	private val slash: String = "(slash)"

	@PostConstruct
	private fun postConstructor() {
		try {
			if (serviceAccountKeyPath.isNotBlank()) {
				val resource = ClassPathResource(serviceAccountKeyPath)
				storage = StorageOptions.newBuilder()
					.setCredentials(GoogleCredentials.fromStream(resource.inputStream))
					.build()
					.service
			}
		} catch (e: Exception) {
			error(e.message!!)
		}
	}

	fun get(fileName: String): FileInfo {
		validateInfo()
		val file = fileName.replace(slash, "/")
		val bucketStorage = storage?.get(bucket)
		val blob = bucketStorage?.get(file)
		return createBlobOut(blob!!)
	}

	fun list(): List<Any> {
		validateInfo()
		val out: MutableList<Any> = ArrayList()
		val bucketStorage = storage?.get(bucket)
		for (blob in bucketStorage?.list()?.iterateAll()!!) {
			out.add(createBlobOut(blob))
		}

		return out
	}

	fun put(
		directory: String,
		contentType: String,
		extension: String,
		base64File: String
	): String {
		validateInfo()
		val fileName = UUID.randomUUID().toString() + extension
		val blobId = BlobId.of(
			bucket,
			directory.replace(slash, "/")
				+ "/" +
				fileName
		)

		val blobInfo = BlobInfo.newBuilder(blobId)
			.setContentType(contentType)
			.build()

		val blob = storage?.create(
			blobInfo,
			Base64.getDecoder().decode(
				base64File
			)
		)

		return blob?.signUrl(
			100000L,
			TimeUnit.DAYS
		).toString()
	}

	fun put(
		directory: String,
		contentType: String,
		name: String,
		extension: String,
		base64File: String
	): String {
		validateInfo()
		val blobId = BlobId.of(
			bucket,
			directory.replace(slash, "/")
				+ "/" +
				"${name}${extension}"
		)

		val blobInfo = BlobInfo.newBuilder(blobId)
			.setContentType(contentType)
			.build()

		val blob = storage?.create(
			blobInfo,
			Base64.getDecoder().decode(
				base64File
			)
		)

		return blob?.signUrl(
			100000L,
			TimeUnit.DAYS
		).toString()
	}

	fun put(
		directory: String,
		mediaType: String,
		name: String,
		extension: String,
		file: InputStream
	): FileInfo {
		validateInfo()
		val fileName = name + extension
		val blobId = BlobId.of(
			bucket,
			directory.replace(slash, "/")
			+ "/" +
			fileName
		)

		val blobInfo = BlobInfo.newBuilder(blobId)
			.setContentType(mediaType)
			.build()

		val blob = storage?.create(
			blobInfo,
			IOUtils.toByteArray(file)
		)

		return FileInfo(
			fileName,
			fileName,
			mediaType,
			Date().toString(),
			blob?.signUrl(
				100000L,
				TimeUnit.DAYS
			).toString()
		)
}

	fun put(
		directory: String,
		contentType: String,
		extension: String,
		file: ByteArray
	): String {
		validateInfo()
		val fileName = UUID.randomUUID().toString() + extension
		val blobId = BlobId.of(
			bucket,
			directory.replace(slash, "/")
				+ "/" +
				fileName
		)

		val blobInfo = BlobInfo.newBuilder(blobId)
			.setContentType(contentType)
			.build()

		val blob = storage?.create(
			blobInfo,
			file
		)

		return blob?.signUrl(
			100000L,
			TimeUnit.DAYS
		).toString()
	}

	fun replace(
		fileName: String,
		contentType: String,
		extension: String,
		base64File: String
	): FileInfo {
		validateInfo()
		val blobId = BlobId.of(
			bucket,
			fileName.replace(slash, "/") +
				extension
		)

		val blobInfo = BlobInfo.newBuilder(blobId)
			.setContentType(contentType)
			.build()

		val blob = storage?.create(
			blobInfo,
			Base64.getDecoder().decode(
				base64File
			)
		)

		return createBlobOut(blob!!)
	}

	fun delete(fileName: String): Map<String, Any> {
		validateInfo()
		val out: MutableMap<String, Any> = LinkedHashMap()
		val file = fileName.replace(slash, "/")
		val blobId = BlobId.of(
			bucket,
			file
		)

		out["fileName"] = file
		out["deleteStatus"] = storage?.delete(blobId)!!
		return out
	}

	private fun createBlobOut(blob: Blob): FileInfo {
		return FileInfo(
			fileName = blob.name,
			refName = "",
			mediaType = blob.contentType,
			createDate = blob.updateTime as String,
			url = blob.signUrl(
				100000L,
				TimeUnit.DAYS
			).toString()
		)
	}

	private fun validateInfo() {
		if (storage == null) {
			throw Exception(
				"The storage.firebase.service-account-key-path " +
				"It isn't include to the application.properties"
			)
		}
	}

}