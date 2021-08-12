package org.pechblenda.auth.repository

import org.pechblenda.auth.entity.IUser

import org.springframework.data.repository.CrudRepository

import java.util.Optional
import java.util.UUID

interface IAuthRepository<T, ID>: CrudRepository<T, ID> {
	fun findByUserName(userName: String): Optional<IUser>
	fun findByActivatePassword(activatePassword: UUID?): Optional<IUser>
	fun findByPassword(password: String): Optional<IUser>
	fun existsByUserName(userName: String): Boolean
	fun existsByEmail(email: String): Boolean
	fun existsByActivatePassword(activatePassword: UUID): Boolean
	fun findByUserNameOrEmail(userName: String): Optional<IUser>
	fun likeByUserName(userName: String): Optional<IUser>
}