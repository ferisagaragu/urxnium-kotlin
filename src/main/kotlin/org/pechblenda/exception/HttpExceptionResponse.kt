package org.pechblenda.exception

import org.slf4j.LoggerFactory
import java.text.SimpleDateFormat
import java.util.Date

import org.springframework.http.ResponseEntity
import org.springframework.web.server.ResponseStatusException


class HttpExceptionResponse {

	private val logger = LoggerFactory.getLogger(HttpExceptionResponse::class.java)


	fun error(e: ResponseStatusException): ResponseEntity<Any> {
		val response: MutableMap<String, Any?> = LinkedHashMap()
		response["timestamp"] = SimpleDateFormat(
			"MM-dd-yyyy  HH:mm:ss a"
		).format(Date())

		response["status"] = e.status.value()
		response["error"] = e.status
		response["message"] = e.reason

		try {
			val developMessage = e.javaClass.getMethod(
				"getDevelopMessage"
			).invoke(e)

			if (developMessage != null) {
				response["developMessage"] = developMessage
			}
		} catch (ex: Exception) {
			logger.info("No develop message send")
		}

		try {
			val developMessage = e.javaClass.getMethod(
				"getFieldNameError"
			).invoke(e)

			if (developMessage != null) {
				response["fieldNameError"] = developMessage
			}
		} catch (ex: Exception) {
			logger.info("No develop message send")
		}

		logger.error(e.message)
		return ResponseEntity(
			response,
			e.status
		)
	}

}