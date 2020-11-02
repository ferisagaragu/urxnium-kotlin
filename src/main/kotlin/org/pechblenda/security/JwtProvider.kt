package org.pechblenda.security

import com.auth0.jwt.JWT

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.SignatureException
import io.jsonwebtoken.UnsupportedJwtException

import org.pechblenda.exception.UnauthenticatedException

import org.slf4j.LoggerFactory

import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component

import java.util.Date

@Component
class JwtProvider(
	private val jwtSecret: String,
	private val jwtExpiration: Int
) {

	companion object {
		private val logger = LoggerFactory.getLogger(JwtProvider::class.java)
	}

	fun generateJwtToken(authentication: Authentication): MutableMap<String, Any> {
		val userPrinciple = authentication.principal as UserDetails
		val tokenAndExpiration: MutableMap<String, Any> = mutableMapOf()
		val expiration = Date(Date().time + jwtExpiration)

		tokenAndExpiration["token"] = Jwts.builder()
			.setSubject(userPrinciple.username)
			.setIssuedAt(Date())
			.setExpiration(expiration)
			.signWith(SignatureAlgorithm.HS512, jwtSecret)
			.compact()
		tokenAndExpiration["expiration"] = jwtExpiration
		tokenAndExpiration["expirationDate"] = expiration

		return tokenAndExpiration
	}

	fun generateJwtTokenNotExpiration(authentication: Authentication): Map<String, Any> {
		val userPrinciple = authentication.principal as UserDetails
		val tokenAndExpiration: MutableMap<String, Any> = mutableMapOf()

		tokenAndExpiration["token"] = Jwts.builder()
			.setSubject(userPrinciple.username)
			.setIssuedAt(Date())
			.signWith(SignatureAlgorithm.HS512, jwtSecret)
			.compact()

		return tokenAndExpiration
	}

	fun generateJwtTokenRefresh(
		authentication: Authentication
	): Map<String, Any> {
		val userPrinciple = authentication.principal as UserDetails
		val expiration = Date(Date().time + 31556900000)
		val token = generateJwtToken(authentication)

		token["refreshToken"] = Jwts.builder()
			.setSubject("${userPrinciple.username}_refresh")
			.setIssuedAt(Date())
			.setExpiration(expiration)
			.signWith(SignatureAlgorithm.HS512, jwtSecret)
			.compact()

		return token
	}

	fun refreshToken(token: String): MutableMap<String, Any> {
		val decodedJWT = JWT.decode(token)
		val tokenAndExpiration: MutableMap<String, Any> = mutableMapOf()
		val expiration = Date(Date().time + jwtExpiration)

		if (decodedJWT.expiresAt < Date()) {
			throw UnauthenticatedException("refresh token has expired")
		}

		if (!decodedJWT.subject.contains("_refresh")) {
			throw UnauthenticatedException("refresh token it's not valid")
		}

		tokenAndExpiration["token"] = Jwts.builder()
			.setSubject(decodedJWT.subject.replace("_refresh", ""))
			.setIssuedAt(Date())
			.setExpiration(expiration)
			.signWith(SignatureAlgorithm.HS512, jwtSecret)
			.compact()
		tokenAndExpiration["expiration"] = jwtExpiration
		tokenAndExpiration["expirationDate"] = expiration

		return tokenAndExpiration
	}

	fun isJwtExpire(token: String): Boolean {
		return JWT.decode(token).expiresAt < Date()
	}

	fun getUserNameFromJwtToken(token: String): String {
		return Jwts.parser()
			.setSigningKey(jwtSecret)
			.parseClaimsJws(token)
			.body
			.subject
	}

	fun validateJwtToken(authToken: String): Boolean {
		try {
			Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken)
			return true
		} catch (e: SignatureException) {
			logger.error("Invalid JWT signature")
		} catch (e: MalformedJwtException) {
			logger.error("Invalid JWT token")
		} catch (e: ExpiredJwtException) {
			logger.error("Expired JWT token")
		} catch (e: UnsupportedJwtException) {
			logger.error("Unsupported JWT token")
		} catch (e: IllegalArgumentException) {
			logger.error("JWT claims string is empty")
		}

		return false
	}

}