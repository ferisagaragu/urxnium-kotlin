package org.pechblenda.core.shared

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import javax.servlet.http.HttpServletRequest

@Component
class Server {

	@Value("\${app.host:}")
	private lateinit var hostName: String

	fun getHost(servletRequest: HttpServletRequest): String {
		if (hostName.isNotBlank()) {
			return hostName
		}

		return if (servletRequest.localAddr.contains("0:0:0"))
			"http://localhost:${servletRequest.localPort}" else
			"http://${servletRequest.localAddr}:${servletRequest.localPort}"
	}

}