package org.pechblenda.style

import java.awt.Color
import java.awt.Font
import java.awt.RenderingHints
import java.awt.image.BufferedImage

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream

import javax.imageio.ImageIO

import org.springframework.beans.factory.annotation.Autowired

class Avatar {

	@Autowired
	private lateinit var color: org.pechblenda.style.Color

	@Throws(Exception::class)
	fun createDefaultAccountImage(initialLetter: Char): InputStream {
		val colorInfo = color.getMaterialColor(CategoryColor.MATERIAL_500)
		return createDefaultAccountImage(initialLetter, colorInfo.color, colorInfo.background)
	}

	@Throws(Exception::class)
	fun createDefaultAccountImage(initialLetter: Char, color: String, background: String): InputStream {
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
		val x = (100 - fm.stringWidth(initialLetter.toString())) / 2
		val y = fm.ascent + (100 - (fm.ascent + fm.descent)) / 2
		g2.drawString(initialLetter.toString(), x, y)

		g2.dispose()

		val os = ByteArrayOutputStream()
		ImageIO.write(bufferedImage, "png", os)
		return ByteArrayInputStream(os.toByteArray())
	}

}