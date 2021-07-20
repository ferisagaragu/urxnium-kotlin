package org.pechblenda.service.refactor

import org.pechblenda.service.Response
import org.pechblenda.service.annotation.Key
import org.pechblenda.service.enum.DefaultValue
import org.pechblenda.service.helper.ResponseList
import org.pechblenda.service.helper.ResponseMap
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

import java.math.BigDecimal
import java.math.BigInteger
import java.net.URL
import java.sql.Blob
import java.sql.Clob
import java.sql.Time
import java.sql.Timestamp

import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

import kotlin.collections.LinkedHashMap

class ResponseRecycle {

	companion object {
		@JvmStatic
		fun response(message: String?, data: Any?, count: Int?, status: HttpStatus): ResponseEntity<Any> {
			val response: MutableMap<String, Any> = LinkedHashMap()
			response["timestamp"] = SimpleDateFormat(
				"MM-dd-yyyy  HH:mm:ss a"
			).format(Date())
			response["status"] = status.value()

			if (message != null) {
				response["message"] = message
			}

			if (count != null) {
				response["count"] = count
			}

			if (data != null) {
				response["data"] = data
			}

			return ResponseEntity(
				response,
				status
			)
		}

		@JvmStatic
		fun asCall(call: String, part: Int): String {
			val callParts = call.split(" as ".toRegex())
			return if (callParts.size == 2) {
				callParts[part].split(" use ".toRegex())[0]
			} else {
				callParts[0].split(" use ".toRegex())[0]
			}
		}

		@JvmStatic
		fun asUse(use: String): String {
			val callParts = use.split(" use ".toRegex())
			return if (callParts.size == 2) {
				callParts[1]
			} else {
				return ""
			}
		}

		@JvmStatic
		fun callMethods(any: Any, out: ResponseMap, vararg callMethods: String) {
			for (callMethod in callMethods) {
				val functionRef: String = asCall(callMethod, 0)
				val functionName: String = asCall(callMethod, 1)

				val function = any::class.java.declaredMethods.filter {
					it.name == functionRef
				}

				if (function.isNotEmpty()) {
					val annotation = function[0].declaredAnnotations.filter {
						it.annotationClass == Key::class
					}

					if (annotation.isNotEmpty()) {
						val key: Key = annotation[0] as Key
						out[key.name] = convertFunctionOut(function[0].invoke(any))
					} else {
						out[functionName] = convertFunctionOut(function[0].invoke(any))
					}
				}
			}
		}

		@JvmStatic
		fun autoCallMethods(any: Any, out: ResponseMap) {
			for (declaredMethod in any::class.java.declaredMethods) {
				val annotation = declaredMethod.declaredAnnotations.filter {
					it.annotationClass == Key::class
				}

				if (annotation.isNotEmpty()) {
					val key: Key = annotation[0] as Key

					if (key.autoCall) {
						val value = try {
							declaredMethod.invoke(any)
						} catch (e: Exception) {
							null
						}

						if (value != null) {
							out[key.name] = convertFunctionOut(value)
						} else {
							when (key.defaultNullValue) {
								DefaultValue.NULL -> out[key.name] = null
								DefaultValue.TEXT -> out[key.name] = ""
								DefaultValue.NUMBER -> out[key.name] = -1
								DefaultValue.BOOLEAN -> out[key.name] = false
								DefaultValue.JSON_OBJECT -> out[key.name] = mapOf<String, Any>()
								DefaultValue.JSON_ARRAY -> out[key.name] = listOf<String>()
							}
						}
					}
				}
			}
		}

		@JvmStatic
		private fun convertFunctionOut(functionValue: Any): Any? {
			if (
				(!isValidType(functionValue)) &&
				(functionValue::class.simpleName != "PersistentBag") &&
				(functionValue::class != List::class) &&
				(functionValue::class != ArrayList::class) &&
				(functionValue::class != ResponseList::class) &&
				(functionValue::class != ResponseMap::class)
			) {
				return Response().toMap(functionValue, false)
			}

			if (functionValue::class.simpleName == "PersistentBag") {
				return Response().toListMap(functionValue as List<Any>, false)
			}

			return functionValue
		}

		@JvmStatic
		fun isValidType(clazz: Any): Boolean {
			if (clazz::class == String::class) {
				return true
			} else if (clazz::class == Char::class) {
				return true
			} else if (clazz::class == Boolean::class) {
				return true
			} else if (clazz::class == Byte::class) {
				return true
			} else if (clazz::class == Short::class) {
				return true
			} else if (clazz::class == Int::class) {
				return true
			} else if (clazz::class == Long::class) {
				return true
			} else if (clazz::class == Float::class) {
				return true
			} else if (clazz::class == Double::class) {
				return true
			} else if (clazz::class == BigInteger::class) {
				return true
			} else if (clazz::class == BigDecimal::class) {
				return true
			} else if (clazz::class == Timestamp::class) {
				return true
			} else if (clazz::class == Time::class) {
				return true
			} else if (
				clazz::class == java.sql.Date::class ||
				clazz::class == java.util.Date::class
			) {
				return true
			}

			return isValidTypeNotNative(clazz)
		}

		@JvmStatic
		fun convertValue(value: Any): Any {
			if (value::class == UUID::class)	{
				return (value as UUID).toString()
			}

			return value
		}

		@JvmStatic
		private fun isValidTypeNotNative(clazz: Any): Boolean {
			if (clazz == Calendar::class) {
				return true
			} else if (clazz::class == GregorianCalendar::class) {
				return true
			} else if (clazz::class == Currency::class) {
				return true
			} else if (clazz::class == Locale::class) {
				return true
			} else if (clazz::class == TimeZone::class) {
				return true
			} else if (clazz::class == URL::class) {
				return true
			} else if (clazz::class == Blob::class) {
				return true
			} else if (clazz::class == Clob::class) {
				return true
			} else if (
				clazz::class == ByteArray::class ||
				clazz::class == Array<Byte>::class
			) {
				return true
			} else if (
				clazz::class == CharArray::class ||
				clazz::class == Array<Char>::class
			) {
				return true
			} else if (clazz::class == UUID::class) {
				return true
			}

			return isValidTypeCollection(clazz)
		}

		@JvmStatic
		private fun isValidTypeCollection(clazz: Any): Boolean {
			if (clazz::class == List::class) {
				return false
			} else if (clazz::class == ArrayList::class) {
				return false
			} else if (clazz::class == Map::class) {
				return false
			} else if (clazz::class.simpleName != "PersistentBag") {
				return false
			} else if (clazz::class == ResponseMap::class) {
				return true
			} else if (clazz::class == ResponseList::class) {
				return true
			} else if (clazz::class == Set::class) {
				return false
			}

			return false
		}

	}

}