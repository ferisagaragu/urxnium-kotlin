package org.pechblenda.rest.helper

class RequestIgnore {

	var ignoreKeys: Array<String>

	constructor(vararg ignoreKeys: String) {
		if (ignoreKeys.isNotEmpty()) {
			this.ignoreKeys = ignoreKeys as Array<String>
		} else {
			this.ignoreKeys = arrayOf()
		}
	}

}