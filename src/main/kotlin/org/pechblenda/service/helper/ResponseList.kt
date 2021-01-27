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

	fun ok(): ResponseEntity<Any> {
		return ResponseRecycle.response(null, this, HttpStatus.OK)
	}

	fun ok(message: String): ResponseEntity<Any> {
		return ResponseRecycle.response(message, this, HttpStatus.OK)
	}

	fun created(): ResponseEntity<Any> {
		return ResponseRecycle.response(null, this, HttpStatus.CREATED)
	}

	fun created(message: String): ResponseEntity<Any> {
		return ResponseRecycle.response(message, this, HttpStatus.CREATED)
	}

	fun response(httpStatus: HttpStatus): ResponseEntity<Any> {
		return ResponseRecycle.response(null, this, HttpStatus.CREATED)
	}

	fun response(message: String, httpStatus: HttpStatus): ResponseEntity<Any> {
		return ResponseRecycle.response(message, this, HttpStatus.CREATED)
	}

}