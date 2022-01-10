package org.pechblenda.core.shared

import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import javax.servlet.http.HttpServletRequest

import org.pechblenda.auth.entity.IUser
import org.pechblenda.style.enums.CategoryColor
import org.pechblenda.style.Color

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class DynamicResources {

	@Autowired
	private lateinit var server: Server

	@Autowired
	private lateinit var color: Color

	@Value("\${app.user-avatar:material}")
	private lateinit var type: String

	fun getUserImageUrl(
		servletRequest: HttpServletRequest,
		user: IUser
	): String {
		if (type == "material") {
			val color = this.color.getMaterialColor(CategoryColor.MATERIAL_500)

			return "${server.getHost(servletRequest)}/rest/auth/generate-profile-image/${user.name[0]}/" +
					URLEncoder.encode(color.color, StandardCharsets.UTF_8.toString()) +
					"/" + URLEncoder.encode(color.background, StandardCharsets.UTF_8.toString())
		} else {
			val color = this.color.getGradientColor()

			return "${server.getHost(servletRequest)}/rest/auth/generate-profile-image/${user.name[0]}/" +
					URLEncoder.encode(color.primaryLeft, StandardCharsets.UTF_8.toString()) +
					"/" + URLEncoder.encode(color.shadeColor, StandardCharsets.UTF_8.toString())
		}
	}

}