package org.pechblenda.util

import java.util.Base64
import java.util.UUID

class Text {

	fun unique(): String {
		val originalInput = UUID.randomUUID().toString().substring(0, 12)
		return Base64.getEncoder().encodeToString(originalInput.toByteArray())
	}

}