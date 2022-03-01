package org.pechblenda.doc.entity

class Rest(
	val title: String,
	val description: String,
	val icon: String,
	val version: String,
	val baseUrl: String,
	val baseUrlProd: String,
	val bookmarks: Array<String>,
	val credentials: List<Credential>?,
	val entities: List<Entity>?,
	val src: ArrayList<Src>
)