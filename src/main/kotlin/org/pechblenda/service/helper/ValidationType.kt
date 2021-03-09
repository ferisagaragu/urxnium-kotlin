package org.pechblenda.service.helper

class ValidationType {

	companion object {
		@JvmStatic
		val NOT_NULL = "NOT_NULL"

		@JvmStatic
		val NOT_BLANK = "NOT_BLANK"

		@JvmStatic
		val EXIST = "EXIST"

		@JvmStatic
		val TEXT = "TEXT"

		@JvmStatic
		val NUMBER = "NUMBER"

		@JvmStatic
		val BOOLEAN = "BOOLEAN"

		@JvmStatic
		val ARRAY = "ARRAY"

		@JvmStatic
		fun min(min: Double): String {
			return "_MIN,${min}"
		}

		@JvmStatic
		fun max(max: Double): String {
			return "_MAX,${max}"
		}

		@JvmStatic
		fun minLength(min: Int): String {
			return "MIN_LENGTH,${min}"
		}

		@JvmStatic
		fun maxLength(max: Int): String {
			return "MAX_LENGTH,${max}"
		}

		@JvmStatic
		fun pattern(regex: String): String {
			return "PATTERN,${regex}"
		}

		@JvmStatic
		fun includes(vararg include: String): String {
			var matches = ""
			include.forEach { include -> matches += "${include}," }

			if (matches.isNotBlank()) {
				matches = matches.substring(0, matches.length - 1)
			}

			return "_?I_°N_!C_\"L_#U_\$D_%E_&S*${matches}"
		}

	}

}