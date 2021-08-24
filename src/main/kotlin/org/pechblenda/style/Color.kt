package org.pechblenda.style

import com.fasterxml.jackson.databind.ObjectMapper

import java.io.BufferedReader
import java.io.InputStreamReader

import java.nio.charset.StandardCharsets

class Color {

	fun getMaterialColor(categoryColor: CategoryColor): MaterialColor {
		val colors = readMaterialColors()
		val colorsOut = ArrayList<MaterialColor>()
		val colorsSelected = when(categoryColor) {
			CategoryColor.MATERIAL_50 -> colors["50"] as List<Map<String, String>>
			CategoryColor.MATERIAL_100 -> colors["100"] as List<Map<String, String>>
			CategoryColor.MATERIAL_200 -> colors["200"] as List<Map<String, String>>
			CategoryColor.MATERIAL_300 -> colors["300"] as List<Map<String, String>>
			CategoryColor.MATERIAL_400 -> colors["400"] as List<Map<String, String>>
			CategoryColor.MATERIAL_500 -> colors["500"] as List<Map<String, String>>
			CategoryColor.MATERIAL_600 -> colors["600"] as List<Map<String, String>>
			CategoryColor.MATERIAL_700 -> colors["700"] as List<Map<String, String>>
			CategoryColor.MATERIAL_800 -> colors["800"] as List<Map<String, String>>
			CategoryColor.MATERIAL_900 -> colors["900"] as List<Map<String, String>>
			CategoryColor.MATERIAL_A100 -> colors["A100"] as List<Map<String, String>>
			CategoryColor.MATERIAL_A200 -> colors["A200"] as List<Map<String, String>>
			CategoryColor.MATERIAL_A400 -> colors["A400"] as List<Map<String, String>>
			CategoryColor.MATERIAL_A700 -> colors["A700"] as List<Map<String, String>>
			CategoryColor.ALL -> getAllColors()
		}

		colorsSelected.forEach { color ->
			colorsOut.add(
				MaterialColor(
					color = color["color"].toString(),
					background = color["background"].toString()
				)
			)
		}

		val randomNumber: Int = 0 + java.util.Random().nextInt(colorsOut.size - 1)
		return colorsOut[randomNumber]
	}

	fun getGradientColor(): GradientColor {
		return when(java.util.Random().nextInt(7)) {
			0 -> GradientColor(
				"#53d3fd",
				"#53d3fd",
				"#38a0f3"
			)
			1 -> GradientColor(
				"#f179ba",
				"#f179ba",
				"#d942c7"
			)
			2 -> GradientColor(
				"#34e5af",
				"#34e5af",
				"#2bccba"
			)
			3 -> GradientColor(
				"#c276f2",
				"#c276f2",
				"#9a479e"
			)
			4 -> GradientColor(
				"#faaf3d",
				"#faaf3d",
				"#f57b37"
			)
			5 -> GradientColor(
				"#fc73ad",
				"#fc73ad",
				"#eb5677"
			)
			6 -> GradientColor(
				"#bbbbbb",
				"#bbbbbb",
				"#8a8a8a"
			)
			7 -> GradientColor(
				"#84a2fe",
				"#84a2fe",
				"#837dfb"
			)
			else -> GradientColor(
				"#53d3fd",
				"#53d3fd",
				"#38a0f3"
			)
		}
	}

	private fun readMaterialColors(): Map<*, *> {
		val isr = InputStreamReader(
			this.javaClass.classLoader.getResourceAsStream("material-colors.json"),
			StandardCharsets.UTF_8
		)
		val br = BufferedReader(isr)
		val mapper = ObjectMapper()
		var fileText = ""

		for (line in br.lines()) {
			fileText += line
		}

		return mapper.readValue(fileText, Map::class.java)
	}

	private fun getAllColors(): List<Map<String, String>> {
		val colors = readMaterialColors()
		val colorsOut = ArrayList<Map<String, String>>()

		for ((k, v) in colors) {
			(v as List<Map<String, String>>).forEach { color ->
				colorsOut.add(color)
			}
		}

		return colorsOut
	}

}