package org.pechblenda.rest.helper


class RequestConvert {

	var field: String = ""
	var dao: Any = Any()
	var removeFunctionName: String = ""
	var message: String = ""
	var type: Int

	constructor(field: String) {
		this.field = field
		this.type = 0
	}

	constructor(field: String, dao: Any) {
		this.field = field
		this.dao = dao
		this.type = 1
	}

	constructor(field: String, dao: Any, message: String) {
		this.field = field
		this.dao = dao
		this.message =  message
		this.type = 1
	}

	constructor(field: String, dao: Any, removeFunctionName: String, message: String) {
		this.field = field
		this.dao = dao
		this.removeFunctionName = removeFunctionName
		this.message =  message
		this.type = 1
	}

}