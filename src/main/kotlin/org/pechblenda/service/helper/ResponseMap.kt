package org.pechblenda.service.helper

import org.pechblenda.service.refactor.ResponseRecycle
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

import kotlin.collections.LinkedHashMap

class ResponseMap: LinkedHashMap<String, Any?>() {

	fun order(vararg order: String): ResponseMap {
		val outOrder = ResponseMap()

		for (s in order) {
			if (this.containsKey(s)) {
				outOrder[s] = this[s]!!
			}
		}

		for ((k, v) in this) {
			if(!outOrder.containsKey(k)) {
				outOrder[k] = v
			}
		}

		return outOrder
	}

	fun include(vararg includes: String): ResponseMap {
		val outInclude = ResponseMap()

		for ((k, v) in this) {
			if(includes.contains(k)) {
				outInclude[k] = v
			}
		}

		return outInclude
	}

	fun exclude(vararg excludes: String): ResponseMap {
		val outExclude = ResponseMap()

		for ((k, v) in this) {
			if(!excludes.contains(k)) {
				outExclude[k] = v
			}
		}

		return outExclude
	}

	fun firstId(): ResponseMap {
		val out = this.order("id")
		return out
	}

	fun firstUuid(): ResponseMap {
		val out = this.order("uuid")
		return out
	}

	fun ok(): ResponseEntity<Any> {
		return ResponseRecycle.response(null, this, null, null, HttpStatus.OK)
	}

	fun ok(message: String): ResponseEntity<Any> {
		return ResponseRecycle.response(message, this, null, null, HttpStatus.OK)
	}

	fun ok(detail: Any?): ResponseEntity<Any> {
		return ResponseRecycle.response(null, this, null, detail, HttpStatus.OK)
	}

	fun ok(message: String, detail: Any?): ResponseEntity<Any> {
		return ResponseRecycle.response(message, this, null, detail, HttpStatus.OK)
	}

	fun created(): ResponseEntity<Any> {
		return ResponseRecycle.response(null, this, null, null, HttpStatus.CREATED)
	}

	fun created(message: String): ResponseEntity<Any> {
		return ResponseRecycle.response(message, this, null, null, HttpStatus.CREATED)
	}

	fun created(detail: Any?): ResponseEntity<Any> {
		return ResponseRecycle.response(null, this, null, detail, HttpStatus.CREATED)
	}

	fun created(message: String, detail: Any?): ResponseEntity<Any> {
		return ResponseRecycle.response(message, this, null, detail, HttpStatus.CREATED)
	}

	fun response(httpStatus: HttpStatus): ResponseEntity<Any> {
		return ResponseRecycle.response(null, this, null, null, httpStatus)
	}

	fun response(message: String, httpStatus: HttpStatus): ResponseEntity<Any> {
		return ResponseRecycle.response(message, this, null, null, httpStatus)
	}

	fun response(message: String, detail: Any?, httpStatus: HttpStatus): ResponseEntity<Any> {
		return ResponseRecycle.response(message, this, null, detail, httpStatus)
	}

	fun json(): LinkedHashMap<String, Any?> {
		return this
	}

}