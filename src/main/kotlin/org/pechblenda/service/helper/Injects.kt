package org.pechblenda.service.helper

class Injects {

	var injects: MutableList<Inject> = mutableListOf()

	constructor(vararg injects: Inject) {
		injects.forEach { inject -> this.injects.add(inject) }
	}

}