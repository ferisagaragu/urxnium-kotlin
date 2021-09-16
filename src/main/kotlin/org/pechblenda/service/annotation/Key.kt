package org.pechblenda.service.annotation

import org.pechblenda.service.enum.DefaultValue
import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Target(ElementType.METHOD, ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
annotation class Key(
	val name: String,
	val autoCall: Boolean = false,
	val defaultNullValue: DefaultValue = DefaultValue.NULL
)