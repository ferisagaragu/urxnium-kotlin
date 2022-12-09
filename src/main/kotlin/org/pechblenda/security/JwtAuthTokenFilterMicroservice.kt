package org.pechblenda.security

import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter

class JwtAuthTokenFilterMicroservice(
	val jwtProvider: JwtProvider,
	val authTokenProxy: IAuthTokenProxy
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
				authTokenProxy.validateToken("Bearer $jwt")

				SecurityContextHolder.getContext().authentication = object : Authentication {

					var authenticate = true

					override fun getAuthorities(): Collection<GrantedAuthority?> {
						val out = arrayListOf<GrantedAuthority>()
						jwtProvider.getAuthoritiesJwtToken(jwt).forEach { it
							out.add(SimpleGrantedAuthority(it))
						}

						return out
					}

					override fun getCredentials(): Collection<GrantedAuthority?> {
						val out = arrayListOf<GrantedAuthority>()
						jwtProvider.getAuthoritiesJwtToken(jwt).forEach { it
							out.add(SimpleGrantedAuthority(it))
						}

						return out
					}

					override fun getDetails(): Any? {
						return WebAuthenticationDetailsSource().buildDetails(request)
					}

					override fun getPrincipal(): String {
						return username
					}

					override fun isAuthenticated(): Boolean {
						return authenticate
					}

					override fun setAuthenticated(isAuthenticated: Boolean) {
						authenticate = isAuthenticated
					}

					override fun getName(): String {
						return username
					}
				}
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