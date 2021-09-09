package org.pechblenda.service

import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.client.j2se.MatrixToImageWriter

import java.io.BufferedReader
import java.io.ByteArrayInputStream

import org.pechblenda.service.helper.ResponseList
import org.pechblenda.service.helper.ResponseMap
import org.pechblenda.service.refactor.ResponseRecycle

import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity

import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.UUID
import java.util.stream.Collectors
import javax.imageio.ImageIO
import javax.servlet.http.HttpServletResponse
import org.apache.commons.io.output.ByteArrayOutputStream

class Response {

	fun ok(): ResponseEntity<Any> {
		return ResponseRecycle.response(null,null, null, HttpStatus.OK)
	}

	fun ok(message: String?): ResponseEntity<Any> {
		return ResponseRecycle.response(message, null, null, HttpStatus.OK)
	}

	fun ok(data: Any?): ResponseEntity<Any> {
		return ResponseRecycle.response(null, data, null, HttpStatus.OK)
	}

	fun ok(message: String?, data: Any?): ResponseEntity<Any> {
		return ResponseRecycle.response(message, data, null, HttpStatus.OK)
	}

	fun created(): ResponseEntity<Any> {
		return ResponseRecycle.response(null,null, null, HttpStatus.CREATED)
	}

	fun created(message: String?): ResponseEntity<Any> {
		return ResponseRecycle.response(message, null, null, HttpStatus.CREATED)
	}

	fun created(data: Any?): ResponseEntity<Any> {
		return ResponseRecycle.response(null, data, null, HttpStatus.CREATED)
	}

	fun created(message: String?, data: Any?): ResponseEntity<Any> {
		return ResponseRecycle.response(message, data, null, HttpStatus.CREATED)
	}

	fun file(mediaType: MediaType, fileName: String, fileData: InputStreamResource): ResponseEntity<Any> {
		return ResponseEntity.ok()
			.contentType(MediaType.parseMediaType(mediaType.type))
			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=${fileName}")
			.body(fileData)
	}

	fun file(mediaType: String, fileName: String, fileData: InputStreamResource): ResponseEntity<Any> {
		return ResponseEntity.ok()
			.contentType(MediaType.parseMediaType(mediaType))
			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=${fileName}")
			.body(fileData)
	}

	fun file(mediaType: MediaType, fileName: String, fileData: InputStream): ResponseEntity<Any> {
		return ResponseEntity.ok()
			.contentType(MediaType.parseMediaType(mediaType.type))
			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=${fileName}")
			.body(InputStreamResource(fileData))
	}

	fun file(mediaType: String, fileName: String, fileData: InputStream): ResponseEntity<Any> {
		return ResponseEntity.ok()
			.contentType(MediaType.parseMediaType(mediaType))
			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=${fileName}")
			.body(InputStreamResource(fileData))
	}

	fun qr(data: String): ResponseEntity<Any> {
		val matrix = MultiFormatWriter().encode(
			data,
			BarcodeFormat.QR_CODE,
			512,
			512
		)
		val os = ByteArrayOutputStream()
		ImageIO.write(MatrixToImageWriter.toBufferedImage(matrix), "png", os)
		val iss: InputStream = ByteArrayInputStream(os.toByteArray())

		return file("image/png", "${UUID.randomUUID()}.png", iss)
	}

	fun html(response: HttpServletResponse, content: String): String {
		response.contentType = "text/plain"
		response.characterEncoding = "UTF-8"

		return content
	}

	fun html(response: HttpServletResponse, inputStream: InputStream): String {
		val text: String = BufferedReader(
			InputStreamReader(
				inputStream,
				StandardCharsets.UTF_8
			)
		).lines().collect(Collectors.joining(""))

		response.contentType = "text/plain"
		response.characterEncoding = "UTF-8"

		return text
	}

	fun toMap(any: Any, vararg callMethods: String): ResponseMap {
		return toMap(any, true, *callMethods)
	}

	fun toListMap(anyList: List<Any>, vararg callMethods: String): ResponseList {
		return toListMap(anyList, true, *callMethods)
	}

	fun toMap(any: Any, autoCallFunctions: Boolean, vararg callMethods: String): ResponseMap {
		val out = ResponseMap()

		for (field in any::class.java.declaredFields) {
			field.isAccessible = true
			val result: Any? = field.get(any)

			if (result != null) {
				if (ResponseRecycle.isValidType(result)) {
					out[field.name] = ResponseRecycle.convertValue(result)
				}
			} else {
				out[field.name] = null
			}
		}

		if (autoCallFunctions) {
			//Aquí se invocan que tiene auto call
			ResponseRecycle.autoCallMethods(any, out)
		}

		//Aquí se invocan los métodos llamados por el desarrollador
		ResponseRecycle.callMethods(any, out, *callMethods)

		return out
	}

	fun toListMap(anyList: List<Any>, autoCallFunctions: Boolean,  vararg callMethods: String): ResponseList {
		val out = ResponseList()

		for (any in anyList) {
			out.add(toMap(any, autoCallFunctions, *callMethods))
		}

		return out
	}

}