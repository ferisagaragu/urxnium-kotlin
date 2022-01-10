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
	constructor(reason: String) : this(HttpStatus.NOT_FOUND, reason)
	constructor(reason: String, developMessage: String): this(HttpStatus.NOT_FOUND, reason) {
		this.developMessage = developMessage
	}

	override fun getStackTrace(): Array<StackTraceElement> {
		return arrayOf()
	}
}