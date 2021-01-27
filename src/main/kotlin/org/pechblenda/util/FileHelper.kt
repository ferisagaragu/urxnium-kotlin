package org.pechblenda.util

import org.springframework.web.multipart.MultipartFile

import java.text.DecimalFormat

class FileHelper {

	fun getSize(file: MultipartFile): String {
		val df = DecimalFormat()
		df.maximumFractionDigits = 3

		return if (file.size / (1024.0 * 1024.0) > 1) {
			"${df.format(file.size / (1024.0 * 1024.0))} MB"
		} else {
			"${df.format(file.size / (1024.0 * 1024.0))} KB"
		}
	}

	fun getExtension(file: MultipartFile): String {
		return getExtension(file.contentType!!)
	}

	fun getExtension(contentType: String): String {
		when (contentType) {
			"application/graphql" -> return ".graphql"
			"application/javascript" -> return ".js"
			"application/json" -> return ".json"
			"application/ld+json" -> return ".jsonld"
			"application/msword" -> return ".doc"
			"application/pdf" -> return ".pdf"
			"application/sql" -> return ".sql"
			"application/vnd.ms-excel" -> return ".xls"
			"application/vnd.ms-powerpoint" -> return ".ppt"
			"application/vnd.oasis.opendocument.text" -> return ".odt"
			"application/vnd.openxmlformats-officedocument.presentationml.presentation" -> return ".pptx"
			"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" -> return ".xlsx"
			"application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> return ".docx"
			"application/xml" -> return ".xml"
			"application/zip" -> return ".zip"
			"application/zstd" -> return ".zst"
			"audio/mpeg" -> return ".mpeg"
			"audio/ogg" -> return ".ogg"
			"audio/mp3" -> return ".mp3"
			"video/mp4" -> return ".mp4"
			"video/avi" -> return ".avi"
			"image/gif" -> return ".gif"
			"image/apng" -> return ".apng"
			"image/flif" -> return ".flif"
			"image/webp" -> return ".webp"
			"image/x-mng" -> return ".msg"
			"image/jpeg" -> return ".jpeg"
			"image/jpg" -> return ".jpg"
			"image/png" -> return ".png"
			"text/css" -> return ".css"
			"text/csv" -> return ".csv"
			"text/html" -> return ".html"
			"text/php" -> return ".php"
			"text/plain" -> return ".txt"
			"text/xml" -> return ".xml"
			"application/x-msdownload" -> return ".exe"
			else -> {
				return ""
			}
		}
	}

}