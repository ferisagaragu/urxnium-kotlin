package org.pechblenda.util

import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder

class Context(
	val authentication: Authentication = SecurityContextHolder.getContext().authentication,
	val userName: String = SecurityContextHolder.getContext().authentication.name
)