package org.pechblenda.auth.dynamic

import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.stream.Collectors
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.InputStreamResource
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

import org.pechblenda.service.Response
import org.pechblenda.core.shared.Server

@CrossOrigin(methods = [
	RequestMethod.GET
])
@RestController
@RequestMapping(name = "Auth", value = ["/rest/auth"])
class AuthResourceController {

	@Autowired
	private lateinit var response: Response

	@Autowired
	private lateinit var server: Server

	@RequestMapping("/sign-in-qr-view/styles.074cacf23e87f9a25ca5.css")
	fun getStyles(response: HttpServletResponse): String {
		val text: String = BufferedReader(
			InputStreamReader(
				this.javaClass.classLoader.getResourceAsStream("qr/styles.074cacf23e87f9a25ca5.css"),
				StandardCharsets.UTF_8
			)
		).lines().collect(Collectors.joining(""))

		response.contentType = "text/plain"
		response.characterEncoding = "UTF-8"
		return text
	}

	@RequestMapping(value = ["/sign-in-qr-view/runtime.9e0f5134175dcff82ffc.js"], produces = ["application/javascript"])
	fun getRuntime(response: HttpServletResponse): String {
		val text: String = BufferedReader(
			InputStreamReader(
				this.javaClass.classLoader.getResourceAsStream(
					"qr/runtime.9e0f5134175dcff82ffc.js"
				),
				StandardCharsets.UTF_8
			)
		).lines().collect(Collectors.joining(""))

		response.contentType = "application/javascript"
		response.characterEncoding = "UTF-8"
		return text
	}

	@RequestMapping(value = ["/sign-in-qr-view/polyfills.c8539b94e427590b980e.js"], produces = ["application/javascript"])
	fun getPolyfills(response: HttpServletResponse): String {
		val text: String = BufferedReader(
			InputStreamReader(
				this.javaClass.classLoader.getResourceAsStream(
					"qr/polyfills.c8539b94e427590b980e.js"
				),
				StandardCharsets.UTF_8
			)
		).lines().collect(Collectors.joining(""))

		response.contentType = "application/javascript"
		response.characterEncoding = "UTF-8"
		return text
	}

	@RequestMapping(value = ["/sign-in-qr-view/main.9e0f88baea7ec04ae91e.js"], produces = ["application/javascript"])
	fun getMain(response: HttpServletResponse): String {
		val text: String = BufferedReader(
			InputStreamReader(
				this.javaClass.classLoader.getResourceAsStream(
					"qr/main.9e0f88baea7ec04ae91e.js"
				),
				StandardCharsets.UTF_8
			)
		).lines().collect(Collectors.joining(""))

		response.contentType = "application/javascript"
		response.characterEncoding = "UTF-8"
		return text
	}

	@RequestMapping(value = ["/sign-in-qr-view/794.6655ecae51e67ea98906.js"], produces = ["application/javascript"])
	fun getCore(response: HttpServletResponse, servletRequest: HttpServletRequest): String {
		val text: String = BufferedReader(
			InputStreamReader(
				this.javaClass.classLoader.getResourceAsStream(
					"qr/794.6655ecae51e67ea98906.js"
				),
				StandardCharsets.UTF_8
			)
		).lines().collect(Collectors.joining("")).toString().replace(
			"http://localhost:5000",
			server.getHost(servletRequest)
		)

		response.contentType = "application/javascript"
		response.characterEncoding = "UTF-8"
		return text
	}

	@RequestMapping(value = ["/sign-in-qr-view/607.76a7552ac89852a84743.js"], produces = ["application/javascript"])
	fun getCoreTwo(response: HttpServletResponse, servletRequest: HttpServletRequest): String {
		val text: String = BufferedReader(
			InputStreamReader(
				this.javaClass.classLoader.getResourceAsStream(
					"qr/607.76a7552ac89852a84743.js"
				),
				StandardCharsets.UTF_8
			)
		).lines().collect(Collectors.joining("")).toString().replace(
			"http://localhost:5000",
			server.getHost(servletRequest)
		)

		response.contentType = "application/javascript"
		response.characterEncoding = "UTF-8"
		return text
	}

	@RequestMapping(value = ["/sign-in-qr-view/418.d8cff9f3c7b848f250fb.js"], produces = ["application/javascript"])
	fun getCoreTree(response: HttpServletResponse, servletRequest: HttpServletRequest): String {
		val text: String = BufferedReader(
			InputStreamReader(
				this.javaClass.classLoader.getResourceAsStream(
					"qr/418.d8cff9f3c7b848f250fb.js"
				),
				StandardCharsets.UTF_8
			)
		).lines().collect(Collectors.joining("")).toString().replace(
			"http://localhost:5000",
			server.getHost(servletRequest)
		)

		response.contentType = "application/javascript"
		response.characterEncoding = "UTF-8"
		return text
	}

	@RequestMapping("/sign-in-qr-view/materialdesignicons-webfont.0e4e0b3da55fa7faa77b.ttf")
	fun getFontOne(): ResponseEntity<Any> {
		return response.file(
			"application/octet-stream",
			"",
			InputStreamResource(
				this.javaClass.classLoader.getResourceAsStream(
					"qr/materialdesignicons-webfont.0e4e0b3da55fa7faa77b.ttf"
				)
			)
		)
	}

	@RequestMapping("/sign-in-qr-view/materialdesignicons-webfont.d8e8e0f7931afa097409.woff")
	fun getFontTwo(): ResponseEntity<Any> {
		return response.file(
			"application/octet-stream",
			"",
			InputStreamResource(
				this.javaClass.classLoader.getResourceAsStream(
					"qr/materialdesignicons-webfont.d8e8e0f7931afa097409.woff"
				)
			)
		)
	}

	@RequestMapping("/sign-in-qr-view/materialdesignicons-webfont.e9db4005489e24809b62.woff2")
	fun getFontTree(): ResponseEntity<Any> {
		return response.file(
			"application/octet-stream",
			"",
			InputStreamResource(
				this.javaClass.classLoader.getResourceAsStream(
					"qr/materialdesignicons-webfont.e9db4005489e24809b62.woff2"
				)
			)
		)
	}

}