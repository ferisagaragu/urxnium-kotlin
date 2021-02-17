package org.pechblenda.bean

import org.pechblenda.auth.service.mail.AuthMail
import org.pechblenda.auth.service.message.AuthMessage
import org.pechblenda.exception.HttpExceptionResponse
import org.pechblenda.mail.GoogleMail
import org.pechblenda.service.Request
import org.pechblenda.service.Response
import org.pechblenda.util.Avatar
import org.pechblenda.util.Report

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class UrxniumBean {

	@Bean
	open fun response(): Response {
		return Response()
	}

	@Bean
	open fun request(): Request {
		return Request()
	}

	@Bean
	open fun httpExceptionResponse(): HttpExceptionResponse {
		return HttpExceptionResponse()
	}

	@Bean
	open fun report(): Report {
		return Report()
	}

	@Bean
	open fun googleMail(): GoogleMail {
		return GoogleMail()
	}

	@Bean
	open fun avatar(): Avatar {
		return Avatar()
	}

	@Bean
	open fun authMail(): AuthMail {
		return AuthMail()
	}

	@Bean
	open fun authMessage(): AuthMessage {
		return AuthMessage()
	}

}