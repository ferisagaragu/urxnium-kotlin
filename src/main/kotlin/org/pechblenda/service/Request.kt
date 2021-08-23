package org.pechblenda.service

import com.fasterxml.jackson.databind.ObjectMapper
import java.util.Date
import java.util.UUID

import org.pechblenda.service.helper.EntityParse
import org.pechblenda.service.helper.ProtectFields
import org.pechblenda.service.helper.Validations

import kotlin.collections.LinkedHashMap
import kotlin.reflect.KClass
import org.pechblenda.exception.BadRequestException
import org.pechblenda.service.helper.SingleValidation
import org.pechblenda.service.helper.Validation

import org.slf4j.LoggerFactory

class Request: LinkedHashMap<String, Any?>() {

	private val mapper = ObjectMapper()

	companion object {
		private val logger = LoggerFactory.getLogger(Request::class.java)
	}

	fun <T> to(kClass: KClass<*>, vararg entityParse: EntityParse): T {
		return this.to(kClass, Validations(), *entityParse)
	}

	fun <T> to(kClass: KClass<*>, validations: Validations, vararg entityParse: EntityParse): T {
		val toSerialize = LinkedHashMap<String, Any?>()

		validations.validate(this)

		entityParse.forEach { entityParse ->
			entityParse.convertEntity(this)
		}

		kClass.java.declaredFields.forEach { member ->
			val name = member.name

			if (this.containsKey(name) && this[name] != null) {
				toSerialize[name] = this[name]
				logger.info("Set in '$name' value '${this[name]}'")
			}
		}

		return mapper.convertValue(
			toSerialize,
			kClass.java
		) as T
	}

	fun <T> to(key: String): T {
		return to(key, null)
	}

	fun <T> to(key: String, singleValidation: SingleValidation?): T {
		if (singleValidation != null) {
			Validations(
				Validation(
					key,
					singleValidation
				)
			).validate(this)
		}

		val value = this[key]

		try {
			return value.toString().toDouble() as T
		} catch (e: NumberFormatException) { }

		try {
			return UUID.fromString(value.toString()) as T
		} catch (e: Exception) { }

		try {
			if (
				value.toString().toLowerCase() == "true" ||
				value.toString().toLowerCase() == "false"
			) {
				return value.toString().toBoolean() as T
			}
		} catch (e: Exception) { }

		return value as T
	}

	fun <T> toList(
		key: String,
		kClass: KClass<*>,
		vararg entityParse: EntityParse
	): List<T> {
		return toList(key, kClass, Validations(), *entityParse)
	}

	fun <T> toList(
		key: String,
		kClass: KClass<*>,
		validations: Validations,
		vararg entityParse: EntityParse
	): List<T> {
		var out = ArrayList<T>()
		val value = if (key.isBlank()) this else this[key]

		if(value != null) {
			(value as List<LinkedHashMap<String, Any>>).forEach {
				item -> out.add(
					toRequest(item).to(kClass, validations, *entityParse)
				)
			}
		}

		return out
	}

	fun <T> merge(entityParse: EntityParse, vararg entityParses: EntityParse): T {
		return merge(entityParse, ProtectFields(), Validations(), *entityParses)
	}

	fun <T> merge(
		entityParse: EntityParse,
		validations: Validations,
		vararg entityParses: EntityParse
	): T {
		return merge(entityParse, ProtectFields(), validations, *entityParses)
	}

	fun <T> merge(
		entityParse: EntityParse,
		protectFields: ProtectFields,
		vararg entityParses: EntityParse
	): T {
		return merge(entityParse, protectFields, Validations(), *entityParses)
	}

	fun <T> merge(
		entityParse: EntityParse,
		protectFields: ProtectFields,
		validations: Validations,
		vararg entityParses: EntityParse
	): T {
		validations.validate(this)

		val mergeData = entityParse.convertEntityMerge(this)
		var entitiesParses = this.to<T>(mergeData::class, *entityParses) as Any

		entitiesParses::class.java.declaredFields.forEach { member ->
			if (this.containsKey(member.name) && (!protectFields.protectField.contains(member.name))) {
				member.isAccessible = true
				val value = member.get(entitiesParses)

				val setField = mergeData::class.java.declaredFields.filter{ field ->
					field.name == member.name
				}[0]

				setField.isAccessible = true
				setField.set(mergeData, value)
				logger.info("Value '${member.name}' was update")
			}
		}

		return mergeData as T
	}

	fun validate(validations: Validations) {
		validations.validate(this)
	}

	fun toJSON(): String {
		val mapper = ObjectMapper()
		return mapper.writeValueAsString(this)
	}

	fun toRequest(json: String): Request {
		return ObjectMapper().readValue(json, Request::class.java)
	}

	fun toRequest(linkedHashMap: LinkedHashMap<String, Any>): Request {
		var out = Request()

		for ((key, value) in linkedHashMap) {
			out[key] = value
		}

		return out
	}

}