package org.pechblenda.service.helper

class ProtectFields {

	var protectField: MutableList<String> = mutableListOf()

	constructor(vararg protectField: ProtectField) {
		protectField.forEach { field -> this.protectField.add(field.name) }
	}

}