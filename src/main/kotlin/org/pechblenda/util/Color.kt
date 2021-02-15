package org.pechblenda.util

import com.fasterxml.jackson.databind.ObjectMapper
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

class Color {

	fun getHexRandomColor(): String {
		val isr = InputStreamReader(
			this.javaClass.classLoader.getResourceAsStream("material-colors.json"),
			StandardCharsets.UTF_8
		)
		val br = BufferedReader(isr)
		val mapper = ObjectMapper()
		val colorsOut = mutableListOf<String>()
		var fileText = ""

		for (line in br.lines()) {
			fileText += line
		}

		var colors = mapper.readValue(fileText, Map::class.java)

		for ((k, v) in colors) {
			(v as List<Map<String, String>>).forEach { color ->
				colorsOut.add(color["background"]!!)
			}
		}

		val randomNumber: Int = 0 + java.util.Random().nextInt(colorsOut.size - 1)
		return colorsOut[randomNumber]
	}

	fun getHexRandomColorAndBackground(): Map<String, String> {
		val isr = InputStreamReader(
			this.javaClass.classLoader.getResourceAsStream("material-colors.json"),
			StandardCharsets.UTF_8
		)
		val br = BufferedReader(isr)
		val mapper = ObjectMapper()
		val colorsOut = mutableListOf<Map<String, String>>()
		var fileText = ""

		for (line in br.lines()) {
			fileText += line
		}

		var colors = mapper.readValue(fileText, Map::class.java)

		for ((k, v) in colors) {
			(v as List<Map<String, String>>).forEach { color ->
				colorsOut.add(color)
			}
		}

		val randomNumber: Int = 0 + java.util.Random().nextInt(colorsOut.size - 1)
		return colorsOut[randomNumber]
	}

}