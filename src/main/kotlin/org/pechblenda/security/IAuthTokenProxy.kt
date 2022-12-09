package org.pechblenda.security

import org.pechblenda.service.InternalResponse

import org.springframework.http.ResponseEntity

interface IAuthTokenProxy {
	fun validateToken(bearerToken: String): ResponseEntity<InternalResponse>
}