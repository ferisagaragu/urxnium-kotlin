package org.pechblenda.util

class Type {

	companion object {
		@JvmStatic
		fun isUUID(uuid: String): Boolean {
			val regex = Regex(
				"^[0-9a-fA-F]{8}-[0-9a-fA-F]" +
				"{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-" +
				"[0-9a-fA-F]{12}\$"
			)

			return regex.containsMatchIn(uuid)
		}

		@JvmStatic
		fun isInteger(int: String): Boolean {
			val regex = Regex(
				"^[0-9]*\$"
			)

			return regex.containsMatchIn(int)
		}
	}

}