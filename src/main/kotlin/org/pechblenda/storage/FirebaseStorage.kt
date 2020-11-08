package org.pechblenda.storage

import com.google.cloud.storage.Blob
import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import org.pechblenda.util.Text

import java.util.*
import java.util.concurrent.TimeUnit

class FirebaseStorage(
	var storage: Storage,
	var bucket: String,
	private val text: Text = Text()
) {

	private val slash: String = "(slash)"

	fun get(fileName: String): Map<String, Any> {
		val file = fileName.replace(slash, "/")
		val bucketStorage = storage[bucket]
		val blob = bucketStorage[file]
		return createBlobOut(blob)
	}

	fun list(): List<Any> {
		val out: MutableList<Any> = ArrayList()
		val bucketStorage = storage[bucket]
		for (blob in bucketStorage.list().iterateAll()) {
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
		val fileName = text.unique() + extension
		val blobId = BlobId.of(
			bucket,
			directory.replace(slash, "/")
				+ "/" +
				fileName
		)

		val blobInfo = BlobInfo.newBuilder(blobId)
			.setContentType(contentType)
			.build()

		val blob = storage.create(
			blobInfo,
			Base64.getDecoder().decode(
				base64File
			)
		)

		return blob.signUrl(
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
		val blobId = BlobId.of(
			bucket,
			directory.replace(slash, "/")
				+ "/" +
				"${name}${extension}"
		)

		val blobInfo = BlobInfo.newBuilder(blobId)
			.setContentType(contentType)
			.build()

		val blob = storage.create(
			blobInfo,
			Base64.getDecoder().decode(
				base64File
			)
		)

		return blob.signUrl(
			100000L,
			TimeUnit.DAYS
		).toString()
	}

	fun put(
		directory: String,
		contentType: String,
		name: String,
		extension: String,
		file: ByteArray
	): String {
		val fileName = name + extension
		val blobId = BlobId.of(
			bucket,
			directory.replace(slash, "/")
			+ "/" +
			fileName
		)

		val blobInfo = BlobInfo.newBuilder(blobId)
			.setContentType(contentType)
			.build()

		val blob = storage.create(
			blobInfo,
			file
		)

		return blob.signUrl(
			100000L,
			TimeUnit.DAYS
		).toString()
}

	fun put(
		directory: String,
		contentType: String,
		extension: String,
		file: ByteArray
	): String {
		val fileName = text.unique() + extension
		val blobId = BlobId.of(
			bucket,
			directory.replace(slash, "/")
				+ "/" +
				fileName
		)

		val blobInfo = BlobInfo.newBuilder(blobId)
			.setContentType(contentType)
			.build()

		val blob = storage.create(
			blobInfo,
			file
		)

		return blob.signUrl(
			100000L,
			TimeUnit.DAYS
		).toString()
	}

	fun replace(
		fileName: String,
		contentType: String,
		extension: String,
		base64File: String
	): Map<String, Any> {
		val blobId = BlobId.of(
			bucket,
			fileName.replace(slash, "/") +
				extension
		)

		val blobInfo = BlobInfo.newBuilder(blobId)
			.setContentType(contentType)
			.build()

		val blob = storage.create(
			blobInfo,
			Base64.getDecoder().decode(
				base64File
			)
		)

		return createBlobOut(blob)
	}

	fun delete(fileName: String): Map<String, Any> {
		val out: MutableMap<String, Any> = LinkedHashMap()
		val file = fileName.replace(slash, "/")
		val blobId = BlobId.of(
			bucket,
			file
		)

		out["fileName"] = file
		out["deleteStatus"] = storage.delete(blobId)
		return out
	}


	private fun createBlobOut(blob: Blob): Map<String, Any> {
		val out: MutableMap<String, Any> = LinkedHashMap()
		out["fileName"] = blob.name
		out["contentType"] = blob.contentType
		out["updateTime"] = blob.updateTime
		out["link"] = blob.signUrl(
			100000L,
			TimeUnit.DAYS
		).toString()
		return out
	}

}