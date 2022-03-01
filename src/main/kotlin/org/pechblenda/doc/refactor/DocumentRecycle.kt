package org.pechblenda.doc.refactor

import com.fasterxml.jackson.databind.ObjectMapper

import org.pechblenda.doc.entity.ApiInfo
import org.pechblenda.doc.annotation.ApiDocumentation
import org.pechblenda.doc.entity.Rest
import org.pechblenda.doc.entity.RestElement
import org.pechblenda.doc.entity.Src
import org.pechblenda.service.Request

import org.springframework.core.io.ClassPathResource
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.beans.factory.annotation.Value

import java.io.BufferedReader
import java.lang.reflect.Method

import javax.servlet.http.HttpServletRequest
import org.springframework.stereotype.Component

import kotlin.reflect.KClass
import org.pechblenda.doc.entity.Attribute
import org.pechblenda.doc.entity.Entity
import org.pechblenda.doc.entity.Step
import org.pechblenda.style.Color
import org.pechblenda.style.enums.CategoryColor

@Component
class DocumentRecycle {

	val mapper = ObjectMapper()

	@Value("\${app.host:}")
	private lateinit var hostName: String

	fun generateDoc(
		apiInfo: ApiInfo,
		entitiesInfo: MutableList<KClass<*>>?,
		controllersInfo: MutableList<KClass<*>>,
		servletRequest: HttpServletRequest
	): LinkedHashMap<String, Any> {
		var out = LinkedHashMap<String, Any>()

		out["rest"] = Rest(
			title = apiInfo.title,
			description = apiInfo.description,
			icon = apiInfo.iconUrl,
			version = apiInfo.version,
			baseUrl = getHost(servletRequest),
			baseUrlProd = getHost(servletRequest),
			bookmarks = arrayOf(),
			credentials = apiInfo.credentials,
			entities = generateEntities(entitiesInfo),
			src = generateSrc(controllersInfo, getHost(servletRequest))
		)

		return out
	}

	private fun generateEntities(entitiesInfo: MutableList<KClass<*>>?): MutableList<Entity>? {
		if (entitiesInfo != null) {
			val out = mutableListOf<Entity>()

			entitiesInfo?.forEach { entityInfo ->
				val entity = Entity(
					entityInfo.simpleName!!,
					Color().getMaterialColor(CategoryColor.MATERIAL_500).background,
					entityInfo.java.declaredFields.map { declaredField ->
						Attribute(
							declaredField.name!!,
							declaredField.type.simpleName
						)
					}
				)

				out.add(entity)
			}

			return out
		}

		return null
	}

	private fun generateSrc(controllersInfo: MutableList<KClass<*>>, host: String): ArrayList<Src> {
		var out = ArrayList<Src>()

		controllersInfo.forEach { controllerInfo ->
			var basePath = ""
			var controllerName = ""
			var restElements = ArrayList<RestElement>()

			controllerInfo.java.annotations.forEach { annotation ->
				if (annotation.annotationClass.java == RequestMapping::class.java) {
					val requestMapping = annotation as RequestMapping
					basePath = getPath(requestMapping.value)
					controllerName = requestMapping.name
				}
			}

			controllerInfo.java.declaredMethods.forEach { method ->
				var pathVariables = ArrayList<org.pechblenda.doc.entity.PathVariable>()
				var pathParams = ArrayList<org.pechblenda.doc.entity.PathVariable>()
				var hasRequestBody = false

				method.parameters.forEach { parameter ->
					parameter.annotations.forEach {  annotation ->
						if (annotation.annotationClass.java == PathVariable::class.java) {
							val pathVariableAnnotation = annotation as PathVariable

							pathVariables.add(
								org.pechblenda.doc.entity.PathVariable(
									name = pathVariableAnnotation.value,
									value = "",
									type = "text",
									required = true
								)
							)
						}

						if (annotation.annotationClass.java == RequestParam::class.java) {
							val pathParamAnnotation = annotation as RequestParam

							pathParams.add(
								org.pechblenda.doc.entity.PathVariable(
									name = pathParamAnnotation.value,
									value = "",
									type = "text",
									required = true
								)
							)
						}

						if (annotation.annotationClass.java == RequestBody::class.java) {
							hasRequestBody = true
						}
					}
				}

				method.annotations.forEach { annotation ->
					if (annotation.annotationClass.java == GetMapping::class.java) {
						val service = annotation as GetMapping

						restElements.add(
							setRestElement(
								method,
								"$basePath${getPath(service.value)}",
								"get",
								pathVariables,
								pathParams,
								hasRequestBody,
								host
							)
						)
					}

					if (annotation.annotationClass.java == PostMapping::class.java) {
						val service = annotation as PostMapping

						restElements.add(
							setRestElement(
								method,
								"$basePath${getPath(service.value)}",
								"post",
								pathVariables,
								pathParams,
								hasRequestBody,
								host
							)
						)
					}

					if (annotation.annotationClass.java == PutMapping::class.java) {
						val service = annotation as PutMapping

						restElements.add(
							setRestElement(
								method,
								"$basePath${getPath(service.value)}",
								"put",
								pathVariables,
								pathParams,
								hasRequestBody,
								host
							)
						)
					}

					if (annotation.annotationClass.java == DeleteMapping::class.java) {
						val service = annotation as DeleteMapping

						restElements.add(
							setRestElement(
								method,
								"$basePath${getPath(service.value)}",
								"delete",
								pathVariables,
								pathParams,
								hasRequestBody,
								host
							)
						)
					}

					if (annotation.annotationClass.java == PatchMapping::class.java) {
						val service = annotation as PatchMapping

						restElements.add(
							setRestElement(
								method,
								"$basePath${getPath(service.value)}",
								"patch",
								pathVariables,
								pathParams,
								hasRequestBody,
								host
							)
						)
					}
				}
			}

			out.add(
				Src(
					controllerName,
					restElements
				)
			)
		}

		return out
	}


	private fun setRestElement(
		method: Method,
		mapping: String,
		access: String,
		pathVariables: ArrayList<org.pechblenda.doc.entity.PathVariable>,
		pathParams: ArrayList<org.pechblenda.doc.entity.PathVariable>,
		hasRequestBody: Boolean,
		host: String
	): RestElement {
		var doc = Request()

		method.annotations.forEach { annotation ->
			if (annotation.annotationClass.java == ApiDocumentation::class.java) {
				val docPath = annotation as ApiDocumentation
				val content: String = ClassPathResource(docPath.path).inputStream.bufferedReader().use(BufferedReader::readText)
				doc = mapper.readValue(content, Request::class.java)
			}
		}

		return RestElement(
			name = "${method.name}",
			authorization = if (doc.containsKey("authorization")) doc["authorization"] as Boolean else false,
			file = if (doc.containsKey("file")) doc["file"] as Boolean else false,
			mapping = mapping,
			access = access,
			bookmark = if (doc.containsKey("bookmark")) doc["bookmark"].toString() else "",
			permissions = if (doc.containsKey("permissions")) doc["permissions"] else null,
			description = if (doc.containsKey("description")) doc["description"].toString() else "",
			html = if (doc.containsKey("html")) doc["html"].toString().replace("\${host}", host) else null,
			steps = if (doc.containsKey("steps"))
				(doc["steps"] as List<Map<String, Any>>).map { step ->
					Step(
						access = if (step.containsKey("access")) step["access"] as String else "",
						mapping = if (step.containsKey("mapping")) (step["mapping"] as String).replace("{host}", host) else ""
					)
				} else null,
			pathVariables = if (doc.containsKey("pathVariables"))
				(doc["pathVariables"] as List<Map<String, Any>>).map { variable ->
					org.pechblenda.doc.entity.PathVariable(
						name = if (variable.containsKey("name")) variable["name"] as String else "",
						value = if (variable.containsKey("value")) variable["value"] as String else "",
						type = if (variable.containsKey("type")) variable["type"] as String else "",
						required = if (variable.containsKey("required")) variable["required"] as Boolean else true
					)
				}
			else if (pathVariables.size == 0) null else pathVariables,
			pathParams = if (doc.containsKey("pathParams"))
				(doc["pathParams"] as List<Map<String, Any>>).map { param ->
					org.pechblenda.doc.entity.PathVariable(
						name = if (param.containsKey("name")) param["name"] as String else "",
						value = if (param.containsKey("value")) param["value"] as String else "",
						type = if (param.containsKey("type")) param["type"] as String else "",
						required = if (param.containsKey("required")) param["required"] as Boolean else true
					)
				}
			else if (pathParams.size == 0) null else pathParams,
			formData = if (doc.containsKey("formData"))
				(doc["formData"] as List<Map<String, Any>>).map { variable ->
					org.pechblenda.doc.entity.PathVariable(
						name = if (variable.containsKey("name")) variable["name"] as String else "",
						value = if (variable.containsKey("value")) variable["value"] as Any else "",
						type = if (variable.containsKey("type")) variable["type"] as String else "",
						required = if (variable.containsKey("required")) variable["required"] as Boolean else true
					)
				}
			else null,
			responseOk = if (doc.containsKey("responseOk"))
				if (doc["responseOk"] is String) doc["responseOk"] as String else
					doc["responseOk"] as MutableMap<String, Any> else null,
			responseCreated = if (doc.containsKey("responseCreated"))
				if (doc["responseCreated"] is String) doc["responseCreated"] as String else
					doc["responseCreated"] as MutableMap<String, Any> else null,
			responseUnauthorized = if (doc.containsKey("responseUnauthorized"))
				if (doc["responseUnauthorized"] is String) doc["responseUnauthorized"] as String else
					doc["responseUnauthorized"] as MutableMap<String, Any> else null,
			responseBadRequest = if (doc.containsKey("responseBadRequest"))
				doc["responseBadRequest"]  as MutableMap<String, Any> else null,
			responseInternalServerError = if (doc.containsKey("responseInternalServerError"))
				doc["responseInternalServerError"]  as MutableMap<String, Any> else null,
			requestBody = when {
				doc.containsKey("requestBody") -> doc["requestBody"]
				hasRequestBody -> mutableMapOf<String, String>()
				else -> null
			}
		)
	}

	private fun getPath(value: Array<String>): String {
		if (value.isEmpty()) {
			return ""
		}

		return value[0]
	}

	private fun getHost(servletRequest: HttpServletRequest): String {
		if (hostName.isNotBlank()) {
			return hostName
		}

		return if (servletRequest.localAddr.contains("0:0:0"))
			"http://localhost:${servletRequest.localPort}" else
			"http://${servletRequest.localAddr}:${servletRequest.localPort}"
	}

}