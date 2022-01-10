package org.pechblenda.schedule.entity

class Event(
	val summary: String,
	val description: String,
	val start: EventDate,
	val end: EventDate,
	val colorId: Int,
	val attendees: MutableList<Attendees>
)