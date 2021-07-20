package org.pechblenda.service

import org.pechblenda.service.helper.ResponseList
import org.pechblenda.service.helper.ResponseMap
import org.pechblenda.service.refactor.ResponseRecycle
import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpHeaders

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity

import java.io.InputStream

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