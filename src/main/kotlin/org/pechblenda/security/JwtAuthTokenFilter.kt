package org.pechblenda.security

import org.slf4j.LoggerFactory

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.web.filter.OncePerRequestFilter

import java.io.IOException

import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtAuthTokenFilter(
	val jwtProvider: JwtProvider,
	var userDetailsService: UserDetailsService
): OncePerRequestFilter() {

	companion object {
		private val logger = LoggerFactory.getLogger(JwtAuthTokenFilter::class.java)
	}


	@Throws(ServletException::class, IOException::class)
	override fun doFilterInternal(
		request: HttpServletRequest,
		response: HttpServletResponse,
		filterChain: FilterChain
	) {
		try {
			val jwt = getJwt(request)
			if (jwt != null && jwtProvider!!.validateJwtToken(jwt)) {
				val username = jwtProvider.getUserNameFromJwtToken(jwt)
				val userDetails: UserDetails = userDetailsService.loadUserByUsername(username)
				val authentication = UsernamePasswordAuthenticationToken(
					userDetails,
					null,
					userDetails.authorities
				)

				authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
				SecurityContextHolder.getContext().authentication = authentication
			}
		} catch (e: Exception) {
			Companion.logger.error("Can NOT set user authentication")
		}

		filterChain.doFilter(request, response)
	}


	private fun getJwt(request: HttpServletRequest): String? {
		val authHeader = request.getHeader("Authorization")

		return if (authHeader != null && authHeader.startsWith("Bearer ")) {
			authHeader.replace("Bearer ", "")
		} else null
	}

}