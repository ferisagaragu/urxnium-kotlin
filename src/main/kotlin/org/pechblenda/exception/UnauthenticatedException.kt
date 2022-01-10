package org.pechblenda.exception

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException


class UnauthenticatedException(
	status: HttpStatus,
	reason: String
) : ResponseStatusException(
	HttpStatus.UNAUTHORIZED,
	reason
) {
	var developMessage: String = ""
	constructor(reason: String) : this(HttpStatus.UNAUTHORIZED, reason)
	constructor(reason: String, developMessage: String): this(HttpStatus.UNAUTHORIZED, reason) {
		this.developMessage = developMessage
	}
}