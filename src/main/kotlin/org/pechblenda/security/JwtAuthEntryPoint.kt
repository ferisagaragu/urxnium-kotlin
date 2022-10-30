package org.pechblenda.security

import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.slf4j.LoggerFactory
import org.springframework.http.MediaType

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value

class JwtAuthEntryPoint: AuthenticationEntryPoint {

	private val logger = LoggerFactory.getLogger(JwtAuthEntryPoint::class.java)

	@Value("\${app.language:en}")
	private lateinit var language: String

	@Value("\${app.formal.language:}")
	private lateinit var formalLanguage: String

	@Value("\${message.auth.unauthorized-resource:}")
	private lateinit var unauthorizedResource: String

	override fun commence(
		request: HttpServletRequest,
		response: HttpServletResponse,
		authException: AuthenticationException
	) {
		logger.error("Unauthorized error: {}", authException.message)
		response.contentType = MediaType.APPLICATION_JSON_VALUE
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED)

		val body: MutableMap<String, Any> = HashMap()
		body["status"] = HttpServletResponse.SC_UNAUTHORIZED
		body["error"] = "Unauthorized"
		body["message"] = getUnauthorizedResource()
		body["path"] = request.servletPath

		val mapper = ObjectMapper()
		mapper.writeValue(response.outputStream, body)
	}

	private fun getUnauthorizedResource(): String {
		return when {
			unauthorizedResource.isNotEmpty() -> unauthorizedResource
			language == "es" ->
				"${if (formalLanguage != "true") "Upss no" else "No"}  estas autorizado para acceder a este recurso"
			else -> "${if (formalLanguage != "true") "Upss you" else "You"} are not authorized to access this resource"
		}
	}

}