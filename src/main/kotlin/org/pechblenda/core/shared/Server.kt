package org.pechblenda.core.shared

import javax.servlet.http.HttpServletRequest

class Server {

	private fun getHost(servletRequest: HttpServletRequest, hostName: String): String {
		if (hostName.isNotBlank()) {
			return hostName
		}

		return if (servletRequest.localAddr.contains("0:0:0"))
			"http://localhost:${servletRequest.localPort}" else
			"http://${servletRequest.localAddr}:${servletRequest.localPort}"
	}

}