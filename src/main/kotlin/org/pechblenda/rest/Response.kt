package org.pechblenda.rest

import org.pechblenda.rest.helper.ResponseList
import org.pechblenda.rest.helper.ResponseMap
import org.pechblenda.rest.helper.ResponseRecycle

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity


class Response {

	fun ok(): ResponseEntity<Any> {
		return ResponseRecycle.response(null,null, HttpStatus.OK)
	}

	fun ok(message: String?): ResponseEntity<Any> {
		return ResponseRecycle.response(message, null, HttpStatus.OK)
	}

	fun ok(data: Any?): ResponseEntity<Any> {
		return ResponseRecycle.response(null, data, HttpStatus.OK)
	}

	fun ok(message: String?, data: Any?): ResponseEntity<Any> {
		return ResponseRecycle.response(message, data, HttpStatus.OK)
	}

	fun created(): ResponseEntity<Any> {
		return ResponseRecycle.response(null,null, HttpStatus.CREATED)
	}

	fun created(message: String?): ResponseEntity<Any> {
		return ResponseRecycle.response(message, null, HttpStatus.CREATED)
	}

	fun created(data: Any?): ResponseEntity<Any> {
		return ResponseRecycle.response(null, data, HttpStatus.CREATED)
	}

	fun created(message: String?, data: Any?): ResponseEntity<Any> {
		return ResponseRecycle.response(message, data, HttpStatus.CREATED)
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
					out[field.name] = result
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