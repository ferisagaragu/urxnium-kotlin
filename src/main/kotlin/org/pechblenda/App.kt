package org.pechblenda

import org.pechblenda.document.GoogleSheet
import org.pechblenda.document.entity.Cell

fun main(args: Array<String>) {

	println(GoogleSheet().generateAuthenticationSheetUrl())
	val token = GoogleSheet().getAuthTokenFromCode("4/0AX4XfWixrvrK3SUAZJl67-BaPMrEvSXYP4_i9LJ6iDdaTQjMviFQhtd7TMkxGV3w1pHqLQ")

	val cells = mutableListOf<Cell>()
	cells.add(Cell("Uno 11"))
	cells.add(Cell("Dos 22"))
	cells.add(Cell("Tres 33"))
	cells.add(Cell("Cuatro 44"))

	try {
		GoogleSheet().findCell(token, "17ZsUJNVxQjF9uoIsY6Txe7P1JtV8dq4IFHBnbKGjdes", "A:A")
	} catch (e: Exception) {
		println(e.message)
	}

}

