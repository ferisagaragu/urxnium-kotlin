package org.pechblenda.doc.entity

class ApiInfo(
	val title: String,
	val description: String,
	val iconUrl: String,
	val version: String,
	val baseUrl: String,
	val credentials: List<Credential>?
)