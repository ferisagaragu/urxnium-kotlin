package org.pechblenda.service

import com.fasterxml.jackson.databind.ObjectMapper

import org.pechblenda.service.helper.EntityParse
import org.pechblenda.service.helper.Validations

import kotlin.collections.LinkedHashMap
import kotlin.reflect.KClass

import org.slf4j.LoggerFactory

class Request: LinkedHashMap<String, Any?>() {

	private val mapper = ObjectMapper()

	companion object {
		private val logger = LoggerFactory.getLogger(Request::class.java)
	}

	fun <T> to(kClass: KClass<*>, vararg entityParse: EntityParse): T {
		return this.to(kClass, Validations(),*entityParse)
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

	fun <T> merge(entityParse: EntityParse): T {
		return merge(entityParse, Validations())
	}

	fun <T> merge(entityParse: EntityParse, validations: Validations): T {
		validations.validate(this)

		val mergeData = entityParse.convertEntityMerge(this)
		val requestClass = mapper.convertValue(
			this,
			mergeData::class.java
		)

		mergeData::class.java.declaredFields.forEach { member ->
			if (this.containsKey(member.name)) {
				val fieldRequest = requestClass::class.java.declaredFields.find {
					field -> field.name == member.name
				}

				if (fieldRequest != null) {
					member.isAccessible = true
					fieldRequest.isAccessible = true

					member.set(mergeData, fieldRequest.get(requestClass))
					logger.info("Value '${fieldRequest.name}' was update")
				}
			}
		}

		return mergeData as T
	}

	fun validate(validations: Validations) {
		validations.validate(this)
	}

}