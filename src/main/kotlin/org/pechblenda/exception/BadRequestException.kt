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
	constructor(reason: String) : this(HttpStatus.BAD_REQUEST, reason)
	constructor(reason: String, developMessage: String): this(HttpStatus.BAD_REQUEST, reason) {
		this.developMessage = developMessage
	}
}