package org.pechblenda.service.helper

class Validation {

	val fieldName: String
	val message: String
	val validationType: MutableList<String>

	constructor(
		fieldName: String,
		message: String,
		vararg validationType: String
	) {
		this.fieldName = fieldName
		this.message = message
		this.validationType = mutableListOf()

		validationType.forEach {
			validationTypeEach -> this.validationType.add(validationTypeEach)
		}
	}

	constructor(fieldName: String, singleValidation: SingleValidation) {
		this.fieldName = fieldName
		this.message = singleValidation.message
		this.validationType = singleValidation.validationType
	}

}