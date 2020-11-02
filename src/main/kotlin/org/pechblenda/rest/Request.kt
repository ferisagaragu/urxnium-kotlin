package org.pechblenda.rest

import com.fasterxml.jackson.databind.ObjectMapper

import org.pechblenda.exception.BadRequestException
import org.pechblenda.rest.helper.*
import java.lang.reflect.Field

import kotlin.collections.LinkedHashMap
import kotlin.reflect.KClass


class Request(
	private val badMessage: String = "field not found"
): LinkedHashMap<String, Any?>() {

	fun toLong(key: String): Long {
		if (!this.containsKey(key)) {
			throw BadRequestException("$key $badMessage")
		}

		if (this[key] !is Long) {
			throw BadRequestException("'$key' not is long")
		}

		return this[key].toString().toLong()
	}

	fun toString(key: String): String {
		if (!this.containsKey(key)) {
			throw BadRequestException("'$key' $badMessage")
		}

		return this[key].toString()
	}

	fun toBoolean(key: String): Boolean {
		if (!this.containsKey(key)) {
			throw BadRequestException("'$key' $badMessage")
		}

		if (this[key] !is Boolean) {
				throw BadRequestException("'$key' not is boolean")
		}

		return this[key].toString().toBoolean()
	}

	fun toMap(key: String): LinkedHashMap<String, Any?> {
		if (!this.containsKey(key)) {
			throw BadRequestException("'$key' $badMessage")
		}

		if (this[key] !is Map<*, *>) {
			throw BadRequestException("'$key' not is map")
		}

		return this[key] as LinkedHashMap<String, Any?>
	}

	fun toList(key: String): List<Request> {
		if (!this.containsKey(key)) {
			throw BadRequestException("'$key' $badMessage")
		}

		if (this[key] !is List<*>) {
			throw BadRequestException("'$key' not is list")
		}

		return this[key] as List<Request>
	}

	fun <T> merge(any: Any, vararg requestConvert: RequestConvert): T {
		return merge(any, HelperDAO(0, Any()), RequestIgnore(), *requestConvert)
	}

	fun <T> merge(any: Any, ignoreKeys: RequestIgnore, vararg requestConvert: RequestConvert): T {
		return merge(any, HelperDAO(0, Any()), ignoreKeys, *requestConvert)
	}

	fun <T> merge(any: Any, helperDAO: HelperDAO, ignoreKeys: RequestIgnore, vararg requestConvert: RequestConvert): T {
		val keys = RequestRecycle.cloneOut(this)

		for (requestConvert in requestConvert) {
			val keyName: String = ResponseRecycle.asCall(requestConvert.field, 0)
			val entityName: String = ResponseRecycle.asCall(requestConvert.field, 1)

			if (keys.containsKey(keyName)) {
				if (keys[keyName] == null) {
					if (helperDAO.id == 0) {
						throw BadRequestException("Custom deletion requires the HelperDAO")
					}

					val methodDrop = helperDAO.dao::class.java.declaredMethods
						.filter { it.name == requestConvert.removeFunctionName }

					if (methodDrop.isNotEmpty()) {
						methodDrop[0].invoke(helperDAO.dao, helperDAO.id)

						val field: Field = any.javaClass.getDeclaredField(entityName)
						field.isAccessible = true
						field.set(any, null) //Es necesario borrarlo en la base y persistirlo
						keys.remove(entityName)
					}
				} else {
					keys[entityName] = keys[keyName]
				}
			}
		}

		val out: Any = to(
			any::class,
			false,
			*requestConvert
		)

		for ((k) in keys) {
			RequestRecycle.mergeData(any, out, k, ignoreKeys)
		}

		return any as T
	}

	fun <T> to(kClass: KClass<*>,vararg requestConvert: RequestConvert): T {
		return to(kClass, true, *requestConvert)
	}

	fun <T> to(kClass: KClass<*>, validateRequired: Boolean,vararg requestConvert: RequestConvert): T {
		val out = RequestRecycle.cloneOut(this)
		val toSerialize = LinkedHashMap<String, Any?>()
		val mapper = ObjectMapper()

		for (requestConvert in requestConvert) {
			val fieldName = ResponseRecycle.asCall(requestConvert.field, 0)
			val fieldAlias = ResponseRecycle.asCall(requestConvert.field, 1)
			val fieldUse = ResponseRecycle.asUse(requestConvert.field) //si no esta vine vacio

			if ((requestConvert.type == 1) && (out[fieldName] != null)) {
				RequestRecycle.asValues(fieldName, fieldAlias, out)	//convert keys to as

				if (out[fieldAlias] is List<*>) {
					val findIds = RequestRecycle.validateValueTypes(fieldName, fieldAlias, fieldUse, out)//aqui uno igual pero que de varios
					val mutableList: MutableList<Any> = mutableListOf()

					for (findId in findIds) {
						mutableList.add(RequestRecycle.callDao(requestConvert, findId))
					}

					out[fieldAlias] = mutableList
				} else {
					val findId = RequestRecycle.validateValueType(fieldName, fieldAlias, fieldUse, out)
					if (findId != null) {
						out[fieldAlias] = RequestRecycle.callDao(requestConvert, findId)
					}
				}
			}
		}

		if (validateRequired) {
			RequestRecycle.validateRequired(kClass, out)
		}

		for (member in kClass.java.declaredFields) {
			if (out.containsKey(member.name) && (out[member.name] != null)) {
				toSerialize[member.name] = out[member.name]
			}
		}

		return mapper.convertValue(
			toSerialize,
			kClass.java
		) as T
	}

	fun toJSON(): String {
		val mapper = ObjectMapper()
		return mapper.writeValueAsString(this)
	}

	fun toRequest(json: String): Request {
		return ObjectMapper().readValue(json, Request::class.java)
	}

	override fun get(key: String): Any? {
		if (!containsKey(key)) {
			throw BadRequestException("'$key' $badMessage")
		}
		return super.get(key)
	}

}