package org.pechblenda.doc.entity

class RestElement(
	val name: String,
	val authorization: Boolean,
	val mapping: String,
	val access: String,
	val bookmark: String,
	val permissions: Array<Any>,
	val description: String,
	val html: String?,
	val pathVariables: List<PathVariable>?,
	val pathParams: List<PathVariable>?,
	val responseOk: MutableMap<String, Any>?,
	val responseBadRequest: MutableMap<String, Any>?,
	val responseInternalServerError: MutableMap<String, Any>?,
	val requestBody: Any?
)