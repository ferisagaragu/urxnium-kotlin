package org.pechblenda.style

import java.awt.Color
import java.awt.Font
import java.awt.GradientPaint
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.URL
import javax.imageio.ImageIO

import org.springframework.beans.factory.annotation.Autowired

class Avatar {

	@Autowired
	private lateinit var color: org.pechblenda.style.Color

	fun generateUserImage(initialLetter: Char): InputStream {
		val colorInfo = color.getMaterialColor(CategoryColor.MATERIAL_500)
		return generateIcon(
			initialLetter.toString(),
			colorInfo.color,
			colorInfo.background,
			false
		)
	}

	fun generateUserImage(
		initialLetter: Char,
		color: String,
		background: String
	): InputStream {
		return generateIcon(
			initialLetter.toString(),
			color,
			background,
			false
		)
	}

	fun generateUserIconImage(icon: String): InputStream {
		val colorInfo = color.getMaterialColor(CategoryColor.MATERIAL_500)
		return generateIcon(
			icon,
			colorInfo.color,
			colorInfo.background,
			true
		)
	}

	fun generateUserIconImage(
		icon: String,
		color: String,
		background: String
	): InputStream {
		return generateIcon(
			icon,
			color,
			background,
			true
		)
	}

	fun generateGradientImage(
		letter: Char,
		primaryLeft: String,
		primaryRight: String,
		shadeColor: String
	): InputStream {
		val primaryLeftColor = Color.decode(primaryLeft)
		val primaryRightColor = Color.decode(primaryRight)
		val shadeColor = Color.decode(shadeColor)
		val image = BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB)
		val g = image.createGraphics()
		val primary = GradientPaint(0f, 0f, primaryLeftColor, 100.toFloat(), 0f, primaryRightColor)
		val rC = shadeColor.red
		val gC = shadeColor.green
		val bC = shadeColor.blue
		val shade = GradientPaint(0f, 0f, Color(rC, gC, bC, 0), 60f, 100f, shadeColor)
		val os = ByteArrayOutputStream()
		val font = Font.createFont(
			Font.PLAIN,
			this.javaClass.classLoader.getResourceAsStream("fonts/JosefinSans.ttf")
		).deriveFont(Font.BOLD, 80f)

		g.paint = primary
		g.fillRect(0, 0, 100, 100)
		g.paint = shade
		g.fillRect(0, 0, 100, 100)

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
		g.font = font
		g.color = Color.WHITE

		if (letter.toString() == "F") {
			g.drawString(letter.toString(), 25, 80)
		} else {
			g.drawString(letter.toString(), 20, 80)
		}

		g.dispose()

		ImageIO.write(image, "png", os)
		return ByteArrayInputStream(os.toByteArray())
	}

	private fun generateIcon(
		initialLetter: String,
		color: String,
		background: String,
		useIcons: Boolean
	): InputStream {
		val bufferedImage = BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB)
		val g2 = bufferedImage.createGraphics()
		val font = if(useIcons)
			Font.createFont(
				Font.PLAIN,
				URL("http://cdn.materialdesignicons.com/5.4.55/fonts/materialdesignicons-webfont.ttf")
					.openStream()
			).deriveFont(Font.BOLD, 80f)
		else
			Font.createFont(Font.PLAIN, this.javaClass.classLoader.getResourceAsStream("fonts/Roboto-Light.ttf"))
				.deriveFont(Font.BOLD, 80f)

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
		g2.color = Color.decode(background)
		g2.fillRect(0, 0, 100, 100)

		g2.color = Color.decode(color)
		g2.font = font
		val fm = g2.fontMetrics
		val x = (100 - fm.stringWidth(initialLetter)) / 2
		val y = fm.ascent + (100 - (fm.ascent + fm.descent)) / 2
		g2.drawString(initialLetter, x, y)

		g2.dispose()

		val os = ByteArrayOutputStream()
		ImageIO.write(bufferedImage, "png", os)
		return ByteArrayInputStream(os.toByteArray())
	}

}