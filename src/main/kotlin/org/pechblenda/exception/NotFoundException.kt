package org.pechblenda.exception

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class NotFoundException(
	status: HttpStatus,
	reason: String
) : ResponseStatusException(
	HttpStatus.NOT_FOUND,
	reason
) {
	var developMessage: String = ""
	var fieldNameError: String = ""
	constructor(reason: String) : this(HttpStatus.NOT_FOUND, reason)
	constructor(reason: String, fieldNameError: String): this(HttpStatus.NOT_FOUND, reason) {
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