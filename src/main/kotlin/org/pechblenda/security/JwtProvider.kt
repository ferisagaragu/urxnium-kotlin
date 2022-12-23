package org.pechblenda.security

import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.Claim
import com.auth0.jwt.interfaces.DecodedJWT

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.SignatureException
import io.jsonwebtoken.UnsupportedJwtException

import org.pechblenda.exception.UnauthenticatedException

import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails

import org.slf4j.LoggerFactory

import java.util.Date
import kotlin.random.Random

class JwtProvider {

	@Value("\${app.auth.jwt-secret:jwt@_/urxnium-secret#}")
	private lateinit var jwtSecret: String

	@Value("\${app.auth.jwt-expiration:18000000}")
	private lateinit var jwtExpiration: String

	companion object {
		private val logger = LoggerFactory.getLogger(JwtProvider::class.java)
	}

	fun generateJwtToken(authentication: Authentication): MutableMap<String, Any> {
		val userPrinciple = authentication.principal as UserDetails
		val tokenAndExpiration: MutableMap<String, Any> = mutableMapOf()
		val expiration = Date(Date().time + jwtExpiration.toInt())
		val authorities: List<String> = userPrinciple.authorities.map { authority -> authority.authority }
		val claims: MutableMap<String, Any> = mutableMapOf()

		claims["authorities"] = authorities

		tokenAndExpiration["token"] = Jwts.builder()
			.setClaims(claims)
			.setSubject(userPrinciple.username)
			.setIssuedAt(Date())
			.setExpiration(expiration)
			.signWith(SignatureAlgorithm.HS512, jwtSecret)
			.compact()
		tokenAndExpiration["expiration"] = jwtExpiration
		tokenAndExpiration["expirationDate"] = expiration.toString()

		return tokenAndExpiration
	}

	fun generateJwtTokenNotExpiration(authentication: Authentication): Map<String, Any> {
		val userPrinciple = authentication.principal as UserDetails
		val tokenAndExpiration: MutableMap<String, Any> = mutableMapOf()
		val authorities: List<String> = userPrinciple.authorities.map { authority -> authority.authority }
		val claims: MutableMap<String, Any> = mutableMapOf()

		claims["authorities"] = authorities

		tokenAndExpiration["token"] = Jwts.builder()
			.setClaims(claims)
			.setSubject(userPrinciple.username)
			.setIssuedAt(Date())
			.signWith(SignatureAlgorithm.HS512, jwtSecret)
			.compact()

		return tokenAndExpiration
	}

	fun generateJwtSecretToken(): String {
		val tokenAndExpiration: MutableMap<String, Any> = mutableMapOf()
		val expiration = Date(Date().time + 1800000)
		val claims: MutableMap<String, Any> = mutableMapOf()

		claims["code"] = "${Random.nextInt(0, 9)}${Random.nextInt(0, 9)} - " +
				"${Random.nextInt(0, 9)}${Random.nextInt(0, 9)} - " +
				"${Random.nextInt(0, 9)}${Random.nextInt(0, 9)}"

		tokenAndExpiration["token"] = Jwts.builder()
			.setClaims(claims)
			.setSubject(jwtSecret)
			.setIssuedAt(Date())
			.setExpiration(expiration)
			.signWith(SignatureAlgorithm.HS512, jwtSecret)
			.compact()
		tokenAndExpiration["expiration"] = 1800000
		tokenAndExpiration["expirationDate"] = expiration.toString()

		return tokenAndExpiration["token"].toString()
	}

	fun generateJwtTokenRefresh(authentication: Authentication): Map<String, Any> {
		val userPrinciple = authentication.principal as UserDetails
		val expiration = Date(Date().time + 31556900000)
		val token = generateJwtToken(authentication)
		val authorities: List<String> = userPrinciple.authorities.map { authority -> authority.authority }
		val claims: MutableMap<String, Any> = mutableMapOf()

		claims["authorities"] = authorities

		token["refreshToken"] = Jwts.builder()
			.setClaims(claims)
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
		val expiration = Date(Date().time + jwtExpiration.toInt())

		if (decodedJWT.expiresAt < Date()) {
			throw UnauthenticatedException("refresh token has expired")
		}

		if (!decodedJWT.subject.contains("_refresh")) {
			throw UnauthenticatedException("refresh token it's not valid")
		}

		val authorities = mutableMapOf<String, Any>()
		authorities["authorities"] = (decodedJWT.claims["authorities"] as Claim).asList(String::class.java)

		tokenAndExpiration["token"] = Jwts.builder()
			.setClaims(authorities)
			.setSubject(decodedJWT.subject.replace("_refresh", ""))
			.setIssuedAt(Date())
			.setExpiration(expiration)
			.signWith(SignatureAlgorithm.HS512, jwtSecret)
			.compact()
		tokenAndExpiration["expiration"] = jwtExpiration
		tokenAndExpiration["expirationDate"] = expiration.toString()

		return tokenAndExpiration
	}

	fun decodeJwt(token: String): DecodedJWT {
		return JWT.decode(token)
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

	fun getAuthoritiesJwtToken(token: String): List<String> {
		return Jwts.parser()
			.setSigningKey(jwtSecret)
			.parseClaimsJws(token)
			.body["authorities"] as List<String>
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

	fun validateJwtSecretToken(secretToken: String): String {
		val claims = decodeJwt(secretToken).claims
		val secret = (claims["sub"] as Claim).asString()
		val code = (claims["code"] as Claim).asString()

		if (secret != jwtSecret || isJwtExpire(secretToken)) {
			throw UnauthenticatedException("Invalid JWT secret")
		}

		return code
	}

}