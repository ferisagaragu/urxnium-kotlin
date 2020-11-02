package org.pechblenda.rest.helper

import org.pechblenda.exception.BadRequestException
import org.pechblenda.rest.Response
import org.pechblenda.rest.annotation.Required

import java.lang.reflect.Field
import java.util.*

import kotlin.collections.LinkedHashMap
import kotlin.reflect.KClass


class RequestRecycle {

	companion object {

		private val response: Response = Response()

		@JvmStatic
		fun asValues(
			fieldName: String,
			fieldAlias: String,
			map: MutableMap<String, Any?>
		) {
			val saveOriginal = map[fieldName]!!
			map.remove(fieldName)
			map[fieldAlias] = saveOriginal
		}

		@JvmStatic
		fun validateValueType(
			fieldName: String,
			key: String,
			fieldUse: String,
			map: MutableMap<String, Any?>
		): Long {
			if (fieldUse.isNotEmpty()) {
				if (map[key] !is Map<*,*>) {
					throw BadRequestException(
						"Field '$fieldName' is not a json object"
					)
				}

				val nestedId = map[key] as Map<*,*>

				if (!nestedId.containsKey(fieldUse)) {
					throw BadRequestException(
						"Field '$fieldName' is not found"
					)
				}

				if (nestedId[fieldUse] !is Int) {
					throw BadRequestException(
						"Field '$fieldName' must be a numeric field"
					)
				}

				return nestedId[fieldUse].toString().toLong()
			}

			if (map[key] !is Int) {
				throw BadRequestException(
					"Field '$fieldName' must be a numeric " +
					"field if the id is nested you can " +
					"test with 'use' and the 'field name'"
				)
			}

			return map[key].toString().toLong()
		}

		@JvmStatic
		fun validateValueTypes(
			fieldName: String,
			key: String,
			fieldUse: String,
			map: MutableMap<String, Any?>
		): MutableList<Long> {
			if (fieldUse.isNotEmpty()) {
				val nestedArray = map[key] as List<*>
				val outIds: MutableList<Long> = mutableListOf()

				for (any in nestedArray) {
					if (any !is Map<*,*>) {
						throw BadRequestException(
							"Field '$fieldName' is not a json array"
						)
					}

					if (!any.containsKey(fieldUse)) {
						throw BadRequestException(
							"Field '$fieldName' is not found"
						)
					}

					if (any[fieldUse] !is Int) {
						throw BadRequestException(
							"Field '$fieldName' must be a numeric field"
						)
					}

					outIds.add(any[fieldUse].toString().toLong())
				}

				return outIds
			}

			if (map[key] is List<*>) {
				val content = map[key] as List<Any>

				for (any in content) {
					if (any !is Int) {
						throw BadRequestException(
							"Field '$fieldName' must be a numeric array " +
							"field if the id is nested you can " +
							"test with 'use' and the 'field name'"
						)
					}
				}
			}

			return map[key] as MutableList<Long>
		}

		@JvmStatic
		fun callDao(
			requestConvert: RequestConvert,
			findId: Long
		): ResponseMap {
			val function = requestConvert.dao::class
				.java.declaredMethods.filter {
					it.name == "findById"
				}[0]

			val optional = function.invoke(
				requestConvert.dao,
				findId
			) as Optional<*>

			return response.toMap(
				optional.orElseThrow {
					BadRequestException(
						if (requestConvert.message.isNotEmpty())
							requestConvert.message
						else
							"Value '${findId}' not found matches"
					)
				},
				false
			)
		}

		@JvmStatic
		fun cloneOut(map: LinkedHashMap<String, Any?>): MutableMap<String, Any?> {
			val out = LinkedHashMap<String, Any?>()
			for ((k, v) in map) {
				out[k] = v
			}
			return out
		}

		@JvmStatic
		fun mergeData(any: Any, out: Any, k: String, ignoreKey: RequestIgnore) {
			if (containsField(any, k)) {
				val field: Field = any::class.java.getDeclaredField(k)
				field.isAccessible = true

				val fieldReplace: Field = out::class.java.getDeclaredField(k)
				fieldReplace.isAccessible = true

				if (!ignoreKey.ignoreKeys.contains(k)) {
					field.set(any, fieldReplace.get(out))
				}
			}
		}

		@JvmStatic
		fun containsField(any: Any, fieldName: String): Boolean {
			for (field in any::class.java.declaredFields) {
				if (fieldName == field.name) {
					return true
				}
			}

			return false
		}

		@JvmStatic
		fun validateRequired(kClass: KClass<*>, out: MutableMap<String, Any?>) {
			for (member in kClass.java.declaredFields) {
				val required = member.declaredAnnotations.filter {
					it.annotationClass == Required::class
				}

				if (required.size > 0) {
					if (!out.containsKey(member.name)) {
						throw BadRequestException((required[0] as Required).message)
					}

					if (out[member.name] == null) {
						throw BadRequestException((required[0] as Required).message)
					}

					if (out[member.name].toString().isEmpty()) {
						throw BadRequestException((required[0] as Required).message)
					}
				}
			}
		}

	}

}