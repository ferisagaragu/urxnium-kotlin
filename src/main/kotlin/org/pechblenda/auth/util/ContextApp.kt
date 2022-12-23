package org.pechblenda.auth.util

import java.util.UUID

import org.pechblenda.auth.entity.IUser
import org.pechblenda.auth.repository.IAuthRepository
import org.pechblenda.auth.service.message.AuthInternalMessage
import org.pechblenda.exception.UnauthenticatedException

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder

class ContextApp {

	@Autowired
	private lateinit var authInternalMessage: AuthInternalMessage

	private var authRepository: Any? = null

	constructor() { }

	constructor(authRepository: Any?) {
		this.authRepository = authRepository
	}

	fun getAuthorizeUser(): IUser? {
		return if (authRepository != null) {
			(authRepository as IAuthRepository<IUser, UUID>).findByUserName(
				SecurityContextHolder.getContext().authentication.name
			).orElseThrow {
				UnauthenticatedException(authInternalMessage.getUnauthenticated())
			}
		} else {
			null
		}
	}

	fun getBearerToken(): String {
		return "Bearer " + (SecurityContextHolder.getContext().authentication.details
			as Map<String, String>)["jwtToken"]!!
	}

	fun getToken(): String {
		return (SecurityContextHolder.getContext().authentication.details
				as Map<String, String>)["jwtToken"]!!
	}

}