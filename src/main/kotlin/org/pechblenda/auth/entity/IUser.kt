package org.pechblenda.auth.entity

import java.util.UUID

interface IUser {
	var uuid: UUID
	var name: String
	var surname: String
	var motherSurname: String
	var userName: String
	var email: String
	var password: String
	var photo: String
	var enabled: Boolean
	var active: Boolean
	var activatePassword: UUID?
}