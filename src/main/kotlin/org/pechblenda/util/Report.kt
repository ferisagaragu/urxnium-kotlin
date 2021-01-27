package org.pechblenda.util

import net.sf.jasperreports.engine.JasperCompileManager
import net.sf.jasperreports.engine.JasperReport
import net.sf.jasperreports.engine.JasperFillManager
import net.sf.jasperreports.engine.JasperExportManager
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource

import org.springframework.core.io.InputStreamResource

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream

class Report {

	fun exportPdf(
		report: InputStream,
		data: MutableList<Any>
	): InputStreamResource {
		if (data.isEmpty()) {
			data.add(mutableMapOf<String, String>())
		}

		val jasperReport: JasperReport = JasperCompileManager.compileReport(report)
		val dataSource = JRBeanCollectionDataSource(data)
		val jasperPrint = JasperFillManager.fillReport(jasperReport, mutableMapOf(), dataSource)
		val outputStream = ByteArrayOutputStream()
		JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream)

		return InputStreamResource(ByteArrayInputStream(outputStream.toByteArray()))
	}

	fun exportPdf(
		report: InputStream,
		params: MutableMap<String, Any>,
		data: MutableList<Any>
	): InputStreamResource {
		if (data.isEmpty()) {
			data.add(mutableMapOf<String, String>())
		}

		val jasperReport: JasperReport = JasperCompileManager.compileReport(report)
		val dataSource = JRBeanCollectionDataSource(data)
		val jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource)
		val outputStream = ByteArrayOutputStream()
		JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream)

		return InputStreamResource(ByteArrayInputStream(outputStream.toByteArray()))
	}

}