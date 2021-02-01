package org.pechblenda.security

class GoogleUser(
	var id: String,
	var locale: String,
	var firstName: String,
	var lastName: String,
	var coverPhoto: String,
	var photo: String,
	var email: String
) {

	constructor(): this(
		id = "",
		locale = "",
		firstName = "",
		lastName = "",
		coverPhoto = "",
		photo = "",
		email = ""
	)

}