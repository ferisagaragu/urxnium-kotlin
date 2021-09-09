package org.pechblenda.schedule.entity

enum class AttendeesStatus(val value: String) {
	NEEDS_ACTION("needsAction"),
	DECLINED("declined"),
	TENTATIVE("tentative"),
	ACCEPTED("accepted")
}