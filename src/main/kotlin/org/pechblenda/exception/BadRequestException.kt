package org.pechblenda.exception

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class BadRequestException(
	status: HttpStatus,
	reason: String
) : ResponseStatusException(
	HttpStatus.BAD_REQUEST,
	reason
) {
	var developMessage: String = ""
	var fieldNameError: String = ""
	constructor(reason: String) : this(HttpStatus.BAD_REQUEST, reason)
	constructor(reason: String, fieldNameError: String): this(HttpStatus.BAD_REQUEST, reason) {
		this.fieldNameError = fieldNameError
	}
	constructor(reason: String, developMessage: String, fieldNameError: String): this(HttpStatus.UNAUTHORIZED, reason) {
		this.developMessage = developMessage
		this.fieldNameError = fieldNameError
	}

	override fun getStackTrace(): Array<StackTraceElement> {
		return arrayOf()
	}
}