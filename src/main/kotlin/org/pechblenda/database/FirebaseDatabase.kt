package org.pechblenda.database

import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

import org.slf4j.LoggerFactory

import java.util.UUID

class FirebaseDatabase {

	private val firebaseDatabase: FirebaseDatabase

	companion object {
		private val logger = LoggerFactory.getLogger(FirebaseDatabase::class.java)
	}

	constructor(options: FirebaseOptions) {
		try {
			FirebaseApp.getInstance()
		} catch (e: Exception) {
			FirebaseApp.initializeApp(options)
			logger.info("Init Firebase app")
		}

		firebaseDatabase = FirebaseDatabase.getInstance()
	}

	fun get(reference: String): DatabaseReference {
		return firebaseDatabase.getReference(reference)
	}

	fun post(reference: String, value: Any) {
		val ref = firebaseDatabase.getReference("${reference}/${UUID.randomUUID()}")
		ref.setValueAsync(value)
	}

	fun put(reference: String, value: Any) {
		val ref = firebaseDatabase.getReference(reference)
		ref.setValueAsync(value)
	}

	fun delete(reference: String) {
		val ref = firebaseDatabase.getReference(reference)
		ref.setValueAsync(null)
	}

}