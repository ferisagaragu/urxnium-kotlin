package org.pechblenda.bean

import org.pechblenda.auth.dynamic.AuthResourceController
import org.pechblenda.auth.service.mail.AuthMail
import org.pechblenda.auth.service.message.AuthMessage
import org.pechblenda.core.shared.DynamicResources
import org.pechblenda.core.shared.Server
import org.pechblenda.doc.refactor.DocumentRecycle
import org.pechblenda.exception.HttpExceptionResponse
import org.pechblenda.hook.SlackAlert
import org.pechblenda.mail.GoogleMail
import org.pechblenda.security.GoogleAuthentication
import org.pechblenda.security.JwtProvider
import org.pechblenda.security.JwtProviderSocket
import org.pechblenda.security.OutlookAuthentication
import org.pechblenda.service.Request
import org.pechblenda.service.Response
import org.pechblenda.storage.FirebaseStorage
import org.pechblenda.storage.XenonStorage
import org.pechblenda.style.Avatar
import org.pechblenda.style.Color
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
	open fun color(): Color {
		return Color()
	}

	@Bean
	open fun authMail(): AuthMail {
		return AuthMail()
	}

	@Bean
	open fun authMessage(): AuthMessage {
		return AuthMessage()
	}

	@Bean
	open fun googleAuthentication(): GoogleAuthentication {
		return GoogleAuthentication()
	}

	@Bean
	open fun outlookAuthentication(): OutlookAuthentication {
		return OutlookAuthentication()
	}

	@Bean
	open fun authResourceController(): AuthResourceController {
		return AuthResourceController()
	}

	@Bean
	open fun jwtProvider(): JwtProvider {
		return JwtProvider()
	}

	@Bean
	open fun jwtProviderSocket(): JwtProviderSocket {
		return JwtProviderSocket()
	}

	@Bean
	open fun xenonStorage(): XenonStorage {
		return XenonStorage()
	}

	@Bean
	open fun firebaseStorage(): FirebaseStorage {
		return FirebaseStorage()
	}

	@Bean
	open fun document(): DocumentRecycle {
		return DocumentRecycle()
	}

	@Bean
	open fun server(): Server {
		return Server()
	}

	@Bean
	open fun slackAlert(): SlackAlert {
		return SlackAlert()
	}

	@Bean
	open fun dynamicResources(): DynamicResources {
		return DynamicResources()
	}

}
