package org.pechblenda.security

import io.jsonwebtoken.Jwts

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component

@Component
class JwtProviderSocket {

	@Value("\${app.auth.jwt-secret:jwt@_/urxnium-secret#}")
	private lateinit var jwtSecret: String

	@Value("\${app.auth.jwt-expiration:18000000}")
	private lateinit var jwtExpiration: String

	@Autowired
	private lateinit var jwtProvider: JwtProvider

	fun getUserNameFromJwtToken(token: String): String {
		return jwtProvider.getUserNameFromJwtToken(token)
	}

	fun authenticateWithToken(token: String, userDetails: UserDetails): Authentication {
		var authToken = !jwtProvider.isJwtExpire(token)

		val user = Jwts.parser()
			.setSigningKey(jwtSecret)
			.parseClaimsJws(token)
			.body
			.subject

		return object : Authentication {
			override fun getAuthorities(): Collection<GrantedAuthority?> {
				return userDetails.authorities
			}

			override fun getCredentials(): Any {
				return null!!
			}

			override fun getDetails(): Any {
				return null!!
			}

			override fun getPrincipal(): Any {
				return null!!
			}

			override fun isAuthenticated(): Boolean {
				return authToken && userDetails.isEnabled
			}

			@Throws(java.lang.IllegalArgumentException::class)
			override fun setAuthenticated(b: Boolean) { }

			override fun getName(): String {
				return user
			}
		}
	}

}