package org.pechblenda.service

import com.fasterxml.jackson.databind.ObjectMapper
import kotlin.reflect.KClass

class InternalResponse(
	var data: Any?
) {

	fun <T> to(kClass: KClass<*>): T {
		val json = ObjectMapper().writeValueAsString(data)
		return Request().toRequest(json).to(kClass) as T
	}

}