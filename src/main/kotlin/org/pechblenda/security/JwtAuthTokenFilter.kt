package org.pechblenda.security

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.util.StringUtils

import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtAuthTokenFilter(
	val jwtProvider: JwtProvider,
	var userDetailsService: UserDetailsService
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
				val userDetails: UserDetails? = userDetailsService?.loadUserByUsername(username)
				val authentication = UsernamePasswordAuthenticationToken(
					userDetails,
					null,
					userDetails?.authorities
				)

				val out = mutableMapOf<String, String>()
				out["jwtToken"] = jwt

				authentication.details = out
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

}