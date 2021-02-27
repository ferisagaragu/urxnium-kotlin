package org.pechblenda.service.helper

import org.pechblenda.exception.BadRequestException
import org.pechblenda.service.Response
import org.pechblenda.service.enum.IdType

import kotlin.collections.LinkedHashMap

import java.util.UUID
import java.util.Optional

class EntityParse {

	private val response = Response()

	private var fieldName: String
	private var convertFieldName: String
	private var dao: Any
	private var idType: IdType

	constructor(fieldName: String, dao: Any, idType: IdType) {
		this.fieldName = fieldName
		this.convertFieldName = ""
		this.dao = dao
		this.idType = idType
	}

	constructor(fieldName: String, convertFieldName: String, dao: Any, idType: IdType) {
		this.fieldName = fieldName
		this.convertFieldName = convertFieldName
		this.dao = dao
		this.idType = idType
	}

	fun convertEntity(serializedEntity: LinkedHashMap<String, Any?>) {
		val function = dao::class
			.java.declaredMethods.filter {
				it.name == "findById"
			}[0]

		val optional = function.invoke(
			dao,
			if (idType == IdType.UUID)
				UUID.fromString(serializedEntity[fieldName].toString())
			else
				serializedEntity[fieldName] as Long
		) as Optional<*>

		val out = optional.orElseThrow {
			BadRequestException(
				"Value '${serializedEntity[fieldName]}' not found matches"
			)
		}

		if (convertFieldName.isNotBlank())
			serializedEntity[convertFieldName] = response.toMap(out, false)
		else
			serializedEntity[fieldName] = response.toMap(out, false)
	}

	fun convertEntityMerge(serializedEntity: LinkedHashMap<String, Any?>): Any {
		val function = dao::class
			.java.declaredMethods.filter {
				it.name == "findById"
			}[0]

		val optional = function.invoke(
			dao,
			if (idType == IdType.UUID)
				UUID.fromString(serializedEntity[fieldName].toString())
			else
				serializedEntity[fieldName] as Long
		) as Optional<*>

		return optional.orElseThrow {
			BadRequestException(
				"Value '${serializedEntity[fieldName]}' not found matches"
			)
		}
	}

}