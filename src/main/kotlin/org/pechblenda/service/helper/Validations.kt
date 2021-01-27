package org.pechblenda.service.helper

import org.pechblenda.exception.BadRequestException
import org.pechblenda.exception.BadRequestGraphQLException
import org.pechblenda.service.enum.ServiceType

import org.slf4j.LoggerFactory

class Validations {

	companion object {
		private val logger = LoggerFactory.getLogger(Validations::class.java)
	}

	private var validations: MutableList<Validation> = mutableListOf()
	private var serviceType: ServiceType

	constructor(vararg validations: Validation) {
		validations.forEach { validation -> this.validations.add(validation) }
		this.serviceType = ServiceType.REST
	}

	constructor(serviceType: ServiceType, vararg validations: Validation) {
		validations.forEach { validation -> this.validations.add(validation) }
		this.serviceType = serviceType
	}

	fun validate(serialized: LinkedHashMap<String, Any?>) {
		this.validations.forEach { validation ->
			validation.validationType.forEach { validationTypeEach ->
				if (validationTypeEach == ValidationType.NOT_NULL) {
					if (serialized[validation.fieldName] == null) {
						logger.error("Non-null validation broke with key ${validation.fieldName}")
						throwException(validation.message)
					}
				}

				if (validationTypeEach == ValidationType.NOT_BLANK) {
					if (serialized[validation.fieldName].toString().isBlank()) {
						logger.error("Not-blank validation was broken with key ${validation.fieldName}")
						throwException(validation.message)
					}
				}

				if (validationTypeEach == ValidationType.EXIST) {
					if (!serialized.containsKey(validation.fieldName)) {
						logger.error("Exist validation was broken with key ${validation.fieldName}")
						throwException(validation.message)
					}
				}

				if (validationTypeEach == ValidationType.TEXT) {
					val type = serialized[validation.fieldName]!!::class.java.typeName

					if (type != "java.lang.String") {
						logger.error("Text validation was broken with key ${validation.fieldName}")
						throwException(validation.message)
					}
				}

				if (validationTypeEach == ValidationType.NUMBER) {
					try {
						serialized[validation.fieldName] as Double
					} catch (e: Exception) {
						logger.error("Number validation was broken with key ${validation.fieldName}")
						throwException(validation.message)
					}
				}

				if (validationTypeEach == ValidationType.BOOLEAN) {
					if (
						!(serialized[validation.fieldName].toString().toLowerCase() == "true" ||
						serialized[validation.fieldName].toString().toLowerCase() == "false")
					) {
						logger.error("Boolean validation was broken with key ${validation.fieldName}")
						throwException(validation.message)
					}
				}

				if (validationTypeEach.contains("_MIN")) {
					if (
						(serialized[validation.fieldName] as Double) <
						convertValueDouble(validationTypeEach)
					) {
						logger.error("Min validation was broken with key ${validation.fieldName}")
						throwException(validation.message)
					}
				}

				if (validationTypeEach.contains("_MAX")) {
					if (
						(serialized[validation.fieldName] as Double) >
						convertValueDouble(validationTypeEach)
					) {
						logger.error("Max validation was broken with key ${validation.fieldName}")
						throwException(validation.message)
					}
				}

				if (validationTypeEach.contains("MIN_LENGTH")) {
					if (
						serialized[validation.fieldName].toString().length <
						convertValueInt(validationTypeEach)
					) {
						logger.error("Min length validation was broken with key ${validation.fieldName}")
						throwException(validation.message)
					}
				}

				if (validationTypeEach.contains("MAX_LENGTH")) {
					if (
						serialized[validation.fieldName].toString().length >
						convertValueInt(validationTypeEach)
					) {
						logger.error("Max length validation was broken with key ${validation.fieldName}")
						throwException(validation.message)
					}
				}

				if (validationTypeEach.contains("PATTERN")) {
					if (
						!convertValueRegex(validationTypeEach).matches(
							serialized[validation.fieldName].toString()
						)
					) {
						logger.error("Pattern validation was broken with key ${validation.fieldName}")
						throwException(validation.message)
					}
				}

				if (validationTypeEach.contains("_?I_°N_!C_\"L_#U_\$D_%E_&S*")) {
					var hasError = true

					convertValueList(validationTypeEach).forEach { include ->
						if (serialized[validation.fieldName] == include) {
							hasError = false
						}
					}

					if (hasError) {
						logger.error("Includes validation was broken with key ${validation.fieldName}")
						throwException(validation.message)
					}
				}
			}
		}
	}

	private fun convertValueInt(validation: String): Int {
		return validation.split(",")[1].toInt()
	}

	private fun convertValueDouble(validation: String): Double {
		return validation.split(",")[1].toDouble()
	}

	private fun convertValueList(validation: String): List<String> {
		val validationOut = validation.split("_?I_°N_!C_\"L_#U_\$D_%E_&S*")

		if (validationOut.size > 1) {
			return validationOut[1].split(",")
		}

		return listOf()
	}

	private fun convertValueRegex(validation: String): Regex {
		return validation.split("PATTERN,")[1].toRegex()
	}

	private fun throwException(message: String) {
		if (ServiceType.REST === serviceType) {
			throw BadRequestException(message)
		} else {
			throw BadRequestGraphQLException(message)
		}
	}

}
