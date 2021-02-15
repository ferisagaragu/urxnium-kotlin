package org.pechblenda.util

import com.fasterxml.jackson.databind.ObjectMapper

import java.awt.Color
import java.awt.Font
import java.awt.RenderingHints
import java.awt.image.BufferedImage

import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

import javax.imageio.ImageIO

class Avatar {

	@Throws(Exception::class)
	fun createDefaultAccountImage(lyrics: String): InputStream {
		val colorInfo = readJsonFile()
		val background = colorInfo["background"]
		val color = colorInfo["color"]

		val bufferedImage = BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB)
		val g2 = bufferedImage.createGraphics()
		val font = Font.createFont(Font.PLAIN, this.javaClass.classLoader.getResourceAsStream("Roboto-Light.ttf"))
		.deriveFont(Font.BOLD, 80f)

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
		g2.color = Color.decode(background)
		g2.fillRect(0, 0, 100, 100)

		g2.color = Color.decode(color)
		g2.font = font
		val fm = g2.fontMetrics
		val x = (100 - fm.stringWidth(lyrics[0].toString())) / 2
		val y = fm.ascent + (100 - (fm.ascent + fm.descent)) / 2
		g2.drawString(lyrics[0].toString(), x, y)

		g2.dispose()

		val os = ByteArrayOutputStream()
		ImageIO.write(bufferedImage, "png", os)
		return ByteArrayInputStream(os.toByteArray())
	}

	@Throws(Exception::class)
	fun createDefaultAccountImage(lyrics: String, color: String, background: String): InputStream {
		val bufferedImage = BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB)
		val g2 = bufferedImage.createGraphics()
		val font = Font.createFont(Font.PLAIN, this.javaClass.classLoader.getResourceAsStream("Roboto-Light.ttf"))
			.deriveFont(Font.BOLD, 80f)

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
		g2.color = Color.decode(background)
		g2.fillRect(0, 0, 100, 100)

		g2.color = Color.decode(color)
		g2.font = font
		val fm = g2.fontMetrics
		val x = (100 - fm.stringWidth(lyrics[0].toString())) / 2
		val y = fm.ascent + (100 - (fm.ascent + fm.descent)) / 2
		g2.drawString(lyrics[0].toString(), x, y)

		g2.dispose()

		val os = ByteArrayOutputStream()
		ImageIO.write(bufferedImage, "png", os)
		return ByteArrayInputStream(os.toByteArray())
	}

	private fun readJsonFile(): Map<String, String> {
		val isr = InputStreamReader(
			this.javaClass.classLoader.getResourceAsStream("material-color-500.json"),
			StandardCharsets.UTF_8
		)
		val br = BufferedReader(isr)
		var fileText = ""

		for (line in br.lines()) {
			fileText += line
		}

		val mapper = ObjectMapper()
		var colors = mapper.readValue(fileText, List::class.java) as List<Map<String, String>>
    val randomNumber: Int = 0 + java.util.Random().nextInt(17)
		return colors[randomNumber]
	}

}