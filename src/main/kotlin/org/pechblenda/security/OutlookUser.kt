package org.pechblenda.security

class OutlookUser(
	var id: String,
	var firstName: String,
	var lastName: String,
	var email: String
) {

	constructor(): this(
		id = "",
		firstName = "",
		lastName = "",
		email = ""
	)

}