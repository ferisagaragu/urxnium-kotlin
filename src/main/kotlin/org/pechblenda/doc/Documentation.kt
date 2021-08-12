package org.pechblenda.doc

import org.pechblenda.doc.entity.ApiInfo
import org.pechblenda.doc.refactor.DocumentRecycle

import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

import java.io.BufferedReader
import java.io.InputStreamReader

import java.nio.charset.StandardCharsets

import java.util.stream.Collectors
import javax.servlet.http.HttpServletRequest

import javax.servlet.http.HttpServletResponse

import org.pechblenda.service.Response
import org.springframework.beans.factory.annotation.Autowired
import kotlin.reflect.KClass

@CrossOrigin(methods = [
	RequestMethod.GET,
	RequestMethod.POST
])
@RestController
@RequestMapping("/api")
class Documentation {

	@Autowired
	private lateinit var documentRecycle: DocumentRecycle

	@Autowired
	private lateinit var resp: Response

	private var apiInfo: ApiInfo
	private var controllersInfo = mutableListOf<KClass<*>>()

	constructor(
		apiInfo: ApiInfo,
		vararg controllerInfo: KClass<*>
	) {
		this.apiInfo = apiInfo
		controllerInfo.forEach { controllerInfo -> controllersInfo.add(controllerInfo) }
	}

	@RequestMapping
	fun getIndex(response: HttpServletResponse): String {
		val resource =
			"<!doctype html>" +
			"<html lang=\"en\">" +
			" <head>" +
			"   <meta charset=\"utf-8\">" +
			"   <title>${apiInfo.title}</title>" +
			"   <base href=\"/api/\">" +
			"   <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">" +
			"   <link rel=\"icon\" type=\"image/x-icon\" href=\"/api/favicon.ico\">" +
			"   <link href=\"https://fonts.googleapis.com/css?family=Roboto:300,400,500&display=swap\" rel=\"stylesheet\">" +
			"   <link href=\"https://fonts.googleapis.com/icon?family=Material+Icons\" rel=\"stylesheet\">" +
			"   <link rel=\"stylesheet\" href=\"/api/styles.css\">" +
			" </head>" +
			" <body class=\"mat-typography\">" +
			"   <app-root></app-root>" +
			"   <script src=\"/api/runtime.js\" defer></script>" +
			"   <script src=\"/api/polyfills.js\" defer></script>" +
			"   <script src=\"/api/main.js\" defer></script>" +
			" </body>" +
			"</html>"

		response.contentType = "text/html";
		response.characterEncoding = "UTF-8";
		return resource
	}

	@RequestMapping("/favicon.ico")
	fun getFavicon(response: HttpServletResponse): ResponseEntity<Any> {
		return resp.file(
			"application/octet-stream",
			"favicon.ico",
			InputStreamResource(this.javaClass.classLoader.getResourceAsStream("api/favicon.ico"))
		)
	}

	@RequestMapping("/styles.css")
	fun getStyles(response: HttpServletResponse): String {
		val text: String = BufferedReader(
			InputStreamReader(
				this.javaClass.classLoader.getResourceAsStream("api/styles.39f0a392bef04324b136.css"),
				StandardCharsets.UTF_8
			)
		).lines().collect(Collectors.joining(""))

		response.contentType = "text/plain"
		response.characterEncoding = "UTF-8"
		return text
	}

	@RequestMapping("/assets/svg/logo.svg")
	fun getLogo(response: HttpServletResponse): String {
		val text: String = BufferedReader(
			InputStreamReader(
				this.javaClass.classLoader.getResourceAsStream("api/assets/svg/logo.svg"),
				StandardCharsets.UTF_8
			)
		).lines().collect(Collectors.joining(""))

		response.contentType = "text/plain"
		response.characterEncoding = "UTF-8"
		return text
	}

	@RequestMapping("/materialdesignicons-webfont.6147fc3741c622c5de2c.woff2")
	fun getMaterialDesignIcons(): ResponseEntity<Any> {
		return resp.file(
			"application/octet-stream",
			"materialdesignicons-webfont.6147fc3741c622c5de2c.woff2",
			InputStreamResource(
				this.javaClass.classLoader.getResourceAsStream(
					"api/materialdesignicons-webfont.6147fc3741c622c5de2c.woff2"
				)
			)
		)
	}

	@RequestMapping("/materialdesignicons-webfont.d06df622bc47f0db9812.woff")
	fun getRobotoFont(): ResponseEntity<Any> {
		return resp.file(
			"application/octet-stream",
			"materialdesignicons-webfont.d06df622bc47f0db9812.woff",
			InputStreamResource(
				this.javaClass.classLoader.getResourceAsStream(
					"api/materialdesignicons-webfont.d06df622bc47f0db9812.woff"
				)
			)
		)
	}

	@RequestMapping("/materialdesignicons-webfont.506bc8215df8660d8f04.ttf")
	fun getRobotoLight(response: HttpServletResponse): ResponseEntity<Any> {
		return resp.file(
			"application/octet-stream",
			"materialdesignicons-webfont.506bc8215df8660d8f04.ttf",
			InputStreamResource(
				this.javaClass.classLoader.getResourceAsStream(
					"api/materialdesignicons-webfont.506bc8215df8660d8f04.ttf"
				)
			)
		)
	}

	@RequestMapping("/Inconsolata-Medium.07bf27c8ebda4ec5febb.ttf")
	fun getRobotoConsola(response: HttpServletResponse): ResponseEntity<Any> {
		return resp.file(
			"application/octet-stream",
			"Inconsolata-Medium.07bf27c8ebda4ec5febb.ttf",
			InputStreamResource(
				this.javaClass.classLoader.getResourceAsStream(
					"api/Inconsolata-Medium.07bf27c8ebda4ec5febb.ttf"
				)
			)
		)
	}

	@RequestMapping(value = ["/runtime.js"], produces = ["application/javascript"])
	fun getRuntime(response: HttpServletResponse): String {
		val text: String = BufferedReader(
			InputStreamReader(
				this.javaClass.classLoader.getResourceAsStream(
					"api/runtime.fe79c52e23e242d57799.js"
				),
				StandardCharsets.UTF_8
			)
		).lines().collect(Collectors.joining(""))

		response.contentType = "application/javascript"
		response.characterEncoding = "UTF-8"
		return text
	}

	@RequestMapping(value = ["/polyfills.js"], produces = ["application/javascript"])
	fun getPolyfills(response: HttpServletResponse): String {
		val text: String = BufferedReader(
			InputStreamReader(
				this.javaClass.classLoader.getResourceAsStream(
					"api/polyfills.35a5ca1855eb057f016a.js"
				),
				StandardCharsets.UTF_8
			)
		).lines().collect(Collectors.joining(""))

		response.contentType = "application/javascript"
		response.characterEncoding = "UTF-8"
		return text
	}

	@RequestMapping(value = ["/main.js"], produces = ["application/javascript"])
	fun getMain(response: HttpServletResponse): String {
		val text: String = BufferedReader(
			InputStreamReader(
				this.javaClass.classLoader.getResourceAsStream(
					"api/main.3e851d2d008e890604bf.js"
				),
				StandardCharsets.UTF_8
			)
		).lines().collect(Collectors.joining(""))

		response.contentType = "application/javascript"
		response.characterEncoding = "UTF-8"
		return text
	}

	@RequestMapping(value = ["/11.ca4ac045fefc597f92ac.js"], produces = ["application/javascript"])
	fun getCode(response: HttpServletResponse): String {
		val text: String = BufferedReader(
			InputStreamReader(
				this.javaClass.classLoader.getResourceAsStream(
					"api/11.ca4ac045fefc597f92ac.js"
				),
				StandardCharsets.UTF_8
			)
		).lines().collect(Collectors.joining(""))

		response.contentType = "application/javascript"
		response.characterEncoding = "UTF-8"
		return text
	}

	@RequestMapping(value = ["/10.e13345689dcabdbb2f44.js"], produces = ["application/javascript"])
	fun getCodeOne(response: HttpServletResponse): String {
		val text: String = BufferedReader(
			InputStreamReader(
				this.javaClass.classLoader.getResourceAsStream(
					"api/10.e13345689dcabdbb2f44.js"
				),
				StandardCharsets.UTF_8
			)
		).lines().collect(Collectors.joining(""))

		response.contentType = "application/javascript"
		response.characterEncoding = "UTF-8"
		return text
	}

	@RequestMapping(value = ["/9.0ce7c57ddae910c1ec83.js"], produces = ["application/javascript"])
	fun getCodeTwo(response: HttpServletResponse): String {
		val text: String = BufferedReader(
			InputStreamReader(
				this.javaClass.classLoader.getResourceAsStream(
					"api/9.0ce7c57ddae910c1ec83.js"
				),
				StandardCharsets.UTF_8
			)
		).lines().collect(Collectors.joining(""))

		response.contentType = "application/javascript"
		response.characterEncoding = "UTF-8"
		return text
	}

	@RequestMapping(value = ["/8.fdb406248258ae77b85e.js"], produces = ["application/javascript"])
	fun getCodeTree(response: HttpServletResponse): String {
		val text: String = BufferedReader(
			InputStreamReader(
				this.javaClass.classLoader.getResourceAsStream(
					"api/8.fdb406248258ae77b85e.js"
				),
				StandardCharsets.UTF_8
			)
		).lines().collect(Collectors.joining(""))

		response.contentType = "application/javascript"
		response.characterEncoding = "UTF-8"
		return text
	}

	@RequestMapping(value = ["/7.c8034a7ad280814ec9ca.js"], produces = ["application/javascript"])
	fun getCodeFour(response: HttpServletResponse): String {
		val text: String = BufferedReader(
			InputStreamReader(
				this.javaClass.classLoader.getResourceAsStream(
					"api/7.c8034a7ad280814ec9ca.js"
				),
				StandardCharsets.UTF_8
			)
		).lines().collect(Collectors.joining(""))

		response.contentType = "application/javascript"
		response.characterEncoding = "UTF-8"
		return text
	}

	@RequestMapping(value = ["/6.462fd2fde57fa640daf4.js"], produces = ["application/javascript"])
	fun getCodeFive(response: HttpServletResponse): String {
		val text: String = BufferedReader(
			InputStreamReader(
				this.javaClass.classLoader.getResourceAsStream(
					"api/6.462fd2fde57fa640daf4.js"
				),
				StandardCharsets.UTF_8
			)
		).lines().collect(Collectors.joining(""))

		response.contentType = "application/javascript"
		response.characterEncoding = "UTF-8"
		return text
	}

	@RequestMapping(value = ["/5.6de5fc77efde924dbe86.js"], produces = ["application/javascript"])
	fun getCodeSix(response: HttpServletResponse): String {
		val text: String = BufferedReader(
			InputStreamReader(
				this.javaClass.classLoader.getResourceAsStream(
					"api/5.6de5fc77efde924dbe86.js"
				),
				StandardCharsets.UTF_8
			)
		).lines().collect(Collectors.joining(""))

		response.contentType = "application/javascript"
		response.characterEncoding = "UTF-8"
		return text
	}

	@RequestMapping(value = ["/4.c98ead447cd0fdec5b45.js"], produces = ["application/javascript"])
	fun getCodeSeven(response: HttpServletResponse): String {
		val text: String = BufferedReader(
			InputStreamReader(
				this.javaClass.classLoader.getResourceAsStream(
					"api/4.c98ead447cd0fdec5b45.js"
				),
				StandardCharsets.UTF_8
			)
		).lines().collect(Collectors.joining(""))

		response.contentType = "application/javascript"
		response.characterEncoding = "UTF-8"
		return text
	}

	@RequestMapping("/assets/data/doc.json")
	fun getDoc(
		response: HttpServletResponse,
		servletRequest: HttpServletRequest
	): ResponseEntity<Any> {
		return ResponseEntity(
			documentRecycle.generateDoc(
				apiInfo,
				controllersInfo,
				servletRequest
			),
			HttpStatus.OK
		)
	}

}