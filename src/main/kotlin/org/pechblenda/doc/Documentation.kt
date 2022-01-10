package org.pechblenda.doc

import com.fasterxml.jackson.databind.ObjectMapper
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
import javax.annotation.PostConstruct
import javax.servlet.http.HttpServletRequest

import javax.servlet.http.HttpServletResponse

import org.pechblenda.service.Response
import org.springframework.beans.factory.annotation.Autowired
import kotlin.reflect.KClass
import org.pechblenda.doc.entity.Credential
import org.springframework.beans.factory.annotation.Value

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

	@Value("\${app.doc.title:Title not set}")
	private lateinit var title: String

	@Value("\${app.doc.description:Description not set}")
	private lateinit var description: String

	@Value("\${app.doc.icon-url:}")
	private lateinit var iconUrl: String

	@Value("\${app.doc.version:0.0.0}")
	private lateinit var version: String

	@Value("\${app.doc.credential.end-point:}")
	private lateinit var endPoint: String

	@Value("\${app.doc.credential.token-mapping:}")
	private lateinit var tokenMapping: String

	@Value("\${app.doc.credentials:[]}")
	private lateinit var credential: String

	private lateinit var apiInfo: ApiInfo
	private var controllersInfo = mutableListOf<KClass<*>>()

	constructor(
		vararg controllerInfo: KClass<*>
	) {
		controllerInfo.forEach { controllerInfo -> controllersInfo.add(controllerInfo) }
	}

	constructor(
		apiInfo: ApiInfo,
		vararg controllerInfo: KClass<*>
	) {
		this.apiInfo = apiInfo
		controllerInfo.forEach { controllerInfo -> controllersInfo.add(controllerInfo) }
	}

	@PostConstruct
	fun postConstruct() {
		val credential = convertCredentials()

		this.apiInfo = ApiInfo(
			title = title,
			description = description,
			iconUrl = iconUrl,
			version = version,
			credentials = credential
		)
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
				this.javaClass.classLoader.getResourceAsStream("api/styles.css"),
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
					"api/runtime.js"
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
					"api/polyfills.js"
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
					"api/main.js"
				),
				StandardCharsets.UTF_8
			)
		).lines().collect(Collectors.joining(""))

		response.contentType = "application/javascript"
		response.characterEncoding = "UTF-8"
		return text
	}

	@RequestMapping(value = ["/11.57afce78780529ea77ad.js"], produces = ["application/javascript"])
	fun getCode(response: HttpServletResponse): String {
		val text: String = BufferedReader(
			InputStreamReader(
				this.javaClass.classLoader.getResourceAsStream(
					"api/11.57afce78780529ea77ad.js"
				),
				StandardCharsets.UTF_8
			)
		).lines().collect(Collectors.joining(""))

		response.contentType = "application/javascript"
		response.characterEncoding = "UTF-8"
		return text
	}

	@RequestMapping(value = ["/10.0770939b2284ac1c1651.js"], produces = ["application/javascript"])
	fun getCodeOne(response: HttpServletResponse): String {
		val text: String = BufferedReader(
			InputStreamReader(
				this.javaClass.classLoader.getResourceAsStream(
					"api/10.0770939b2284ac1c1651.js"
				),
				StandardCharsets.UTF_8
			)
		).lines().collect(Collectors.joining(""))

		response.contentType = "application/javascript"
		response.characterEncoding = "UTF-8"
		return text
	}

	@RequestMapping(value = ["/9.515d17f27fd350df413e.js"], produces = ["application/javascript"])
	fun getCodeTwo(response: HttpServletResponse): String {
		val text: String = BufferedReader(
			InputStreamReader(
				this.javaClass.classLoader.getResourceAsStream(
					"api/9.515d17f27fd350df413e.js"
				),
				StandardCharsets.UTF_8
			)
		).lines().collect(Collectors.joining(""))

		response.contentType = "application/javascript"
		response.characterEncoding = "UTF-8"
		return text
	}

	@RequestMapping(value = ["/8.ee0bba644cff1316e349.js"], produces = ["application/javascript"])
	fun getCodeTree(response: HttpServletResponse): String {
		val text: String = BufferedReader(
			InputStreamReader(
				this.javaClass.classLoader.getResourceAsStream(
					"api/8.ee0bba644cff1316e349.js"
				),
				StandardCharsets.UTF_8
			)
		).lines().collect(Collectors.joining(""))

		response.contentType = "application/javascript"
		response.characterEncoding = "UTF-8"
		return text
	}

	@RequestMapping(value = ["/7.6014635581fcf23cbdf8.js"], produces = ["application/javascript"])
	fun getCodeFour(response: HttpServletResponse): String {
		val text: String = BufferedReader(
			InputStreamReader(
				this.javaClass.classLoader.getResourceAsStream(
					"api/7.6014635581fcf23cbdf8.js"
				),
				StandardCharsets.UTF_8
			)
		).lines().collect(Collectors.joining(""))

		response.contentType = "application/javascript"
		response.characterEncoding = "UTF-8"
		return text
	}

	@RequestMapping(value = ["/6.4b2438d9493c7c98df7f.js"], produces = ["application/javascript"])
	fun getCodeFive(response: HttpServletResponse): String {
		val text: String = BufferedReader(
			InputStreamReader(
				this.javaClass.classLoader.getResourceAsStream(
					"api/6.4b2438d9493c7c98df7f.js"
				),
				StandardCharsets.UTF_8
			)
		).lines().collect(Collectors.joining(""))

		response.contentType = "application/javascript"
		response.characterEncoding = "UTF-8"
		return text
	}

	@RequestMapping(value = ["/5.99733c2c7a4b2a0516e8.js"], produces = ["application/javascript"])
	fun getCodeSix(response: HttpServletResponse): String {
		val text: String = BufferedReader(
			InputStreamReader(
				this.javaClass.classLoader.getResourceAsStream(
					"api/5.99733c2c7a4b2a0516e8.js"
				),
				StandardCharsets.UTF_8
			)
		).lines().collect(Collectors.joining(""))

		response.contentType = "application/javascript"
		response.characterEncoding = "UTF-8"
		return text
	}

	@RequestMapping(value = ["/4.d90944a58a1c8f846253.js"], produces = ["application/javascript"])
	fun getCodeSeven(response: HttpServletResponse): String {
		val text: String = BufferedReader(
			InputStreamReader(
				this.javaClass.classLoader.getResourceAsStream(
					"api/4.d90944a58a1c8f846253.js"
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

	private fun convertCredentials(): List<Credential> {
		val mapper = ObjectMapper()
		val credentials = mapper.readValue(credential, List::class.java)
		val out = mutableListOf<Credential>()

		credentials.forEach { credential ->
			val data = credential as Map<String, Any>

			out.add(
				Credential(
					name = "${data["name"]}",
					endPoint = endPoint,
					bodyRequest = data,
					tokenMapping = tokenMapping
				)
			)
		}

		return out
	}

}