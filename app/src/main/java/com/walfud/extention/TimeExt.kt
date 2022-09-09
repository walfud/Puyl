package com.walfud.extention

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private const val DATE_PATTERN = "yyyy-MM-dd"
private const val TIME_PATTERN_24 = "HH:mm:ss"

fun LocalDate.toSimpleString(): String = DateTimeFormatter.ofPattern(DATE_PATTERN).format(this).toString()
fun LocalTime.toSimpleString(): String = DateTimeFormatter.ofPattern(TIME_PATTERN_24).format(this).toString()