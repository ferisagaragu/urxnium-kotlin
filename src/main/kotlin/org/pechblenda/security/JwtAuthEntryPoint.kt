package org.pechblenda.security

import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtAuthEntryPoint: AuthenticationEntryPoint {

	override fun commence(
		request: HttpServletRequest,
		response: HttpServletResponse,
		authenticationException: AuthenticationException
	) {
		response.sendError(
			HttpServletResponse.SC_UNAUTHORIZED,
			authenticationException.message
		)
	}

}