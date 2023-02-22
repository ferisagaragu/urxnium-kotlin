package org.pechblenda.security


import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.util.StringUtils
import org.springframework.security.core.authority.SimpleGrantedAuthority

import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.pechblenda.exception.BadRequestException

import java.util.Optional

class SimpleJwtAuthTokenFilter(
	val jwtProvider: JwtProvider,
	val dao: Any,
	val methodName: String,
	val userNotFoundMessage: String
): OncePerRequestFilter() {

	override fun doFilterInternal(
		request: HttpServletRequest,
		response: HttpServletResponse,
		filterChain: FilterChain
	) {
		try {
			val jwt: String? = parseJwt(request)
			if (jwt != null && jwtProvider.validateJwtToken(jwt)) {
				val username: String = jwtProvider.getUserNameFromJwtToken(jwt)
				callDaoValidator(username
				)
				val authorities = jwtProvider.getAuthoritiesJwtToken(jwt).map{ SimpleGrantedAuthority(it) }
				val out = mutableMapOf<String, String>()

				out["jwtToken"] = jwt

				val authentication = AuthHelper().generateAuthentication(username, authorities, out)

				SecurityContextHolder.getContext().authentication = authentication
			}
		} catch (e: Exception) {
			logger.error("Cannot set user authentication: {}", e)
		}
		filterChain.doFilter(request, response)
	}

	private fun parseJwt(request: HttpServletRequest): String? {
		val headerAuth = request.getHeader("Authorization")
		return if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
			headerAuth.substring(7, headerAuth.length)
		} else null
	}

	private fun callDaoValidator(userName: String) {
		val function = dao::class
			.java.declaredMethods.filter {
				it.name == methodName
			}[0]

		val optional = function.invoke(
			dao,
			userName
		) as Optional<*>

		optional.orElseThrow { BadRequestException(userNotFoundMessage) }
	}

}