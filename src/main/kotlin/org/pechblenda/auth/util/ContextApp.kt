package org.pechblenda.auth.util

import java.util.UUID

import org.pechblenda.auth.entity.IUser
import org.pechblenda.auth.repository.IAuthRepository
import org.pechblenda.auth.service.message.AuthMessage
import org.pechblenda.exception.UnauthenticatedException

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder

class ContextApp {

	@Autowired
	private lateinit var authMessage: AuthMessage

	private var authRepository: Any

	constructor(authRepository: Any) {
		this.authRepository = authRepository
	}

	fun getAuthorizeUser(): IUser {
		val user = (authRepository as IAuthRepository<IUser, UUID>).findByUserName(
			SecurityContextHolder.getContext().authentication.name
		).orElseThrow {
			UnauthenticatedException(authMessage.getUnauthenticated())
		}

		return user
	}

}