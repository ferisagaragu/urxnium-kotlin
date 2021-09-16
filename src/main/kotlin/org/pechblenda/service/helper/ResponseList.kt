package org.pechblenda.service.helper

import org.pechblenda.service.refactor.ResponseRecycle
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

import kotlin.collections.ArrayList

class ResponseList: ArrayList<ResponseMap>() {

	fun order(vararg order: String): ResponseList {
		val outOrder = ResponseList()

		for (responseMap in this) {
			outOrder.add(responseMap.order(*order))
		}

		return outOrder
	}

	fun include(vararg includes: String): ResponseList {
		val outIncludes = ResponseList()

		for (responseMap in this) {
			outIncludes.add(responseMap.include(*includes))
		}

		return outIncludes
	}

	fun exclude(vararg excludes: String): ResponseList {
		val outExcludes = ResponseList()

		for (responseMap in this) {
			outExcludes.add(responseMap.exclude(*excludes))
		}

		return outExcludes
	}

	fun firstId(): ResponseList {
		return this.order("id")
	}

	fun firstUuid(): ResponseList {
		return this.order("uuid")
	}

	fun ok(): ResponseEntity<Any> {
		return ResponseRecycle.response(null, this, this.size, null, HttpStatus.OK)
	}

	fun ok(message: String): ResponseEntity<Any> {
		return ResponseRecycle.response(message, this, this.size, null, HttpStatus.OK)
	}

	fun ok(details: Any?): ResponseEntity<Any> {
		return ResponseRecycle.response(null, this, this.size, details, HttpStatus.OK)
	}

	fun ok(message: String, details: Any?): ResponseEntity<Any> {
		return ResponseRecycle.response(message, this, this.size, details, HttpStatus.OK)
	}

	fun created(): ResponseEntity<Any> {
		return ResponseRecycle.response(null, this, this.size, null, HttpStatus.CREATED)
	}

	fun created(message: String): ResponseEntity<Any> {
		return ResponseRecycle.response(message, this, this.size, null, HttpStatus.CREATED)
	}

	fun created(details: Any?): ResponseEntity<Any> {
		return ResponseRecycle.response(null, this, this.size, details, HttpStatus.CREATED)
	}

	fun created(message: String, details: Any?): ResponseEntity<Any> {
		return ResponseRecycle.response(message, this, this.size, details, HttpStatus.CREATED)
	}

	fun response(httpStatus: HttpStatus): ResponseEntity<Any> {
		return ResponseRecycle.response(null, this, this.size, null, httpStatus)
	}

	fun response(message: String, httpStatus: HttpStatus): ResponseEntity<Any> {
		return ResponseRecycle.response(message, this, this.size, null, httpStatus)
	}

	fun response(message: String, details: Any?, httpStatus: HttpStatus): ResponseEntity<Any> {
		return ResponseRecycle.response(message, this, this.size, details, httpStatus)
	}

	fun json(): List<LinkedHashMap<String, Any?>> {
		return this.map { item -> item as LinkedHashMap<String, Any?> }
	}

}