package org.pechblenda.service.helper

class SingleValidation {

	val message: String
	val validationType: MutableList<String>

	constructor(
		message: String,
		vararg validationType: String
	) {
		this.message = message
		this.validationType = mutableListOf()

		validationType.forEach {
			validationTypeEach -> this.validationType.add(validationTypeEach)
		}
	}

}