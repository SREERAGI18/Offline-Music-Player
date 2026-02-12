package com.example.offlinemusicplayer.util

import java.text.DecimalFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import kotlin.math.log10
import kotlin.math.pow
import kotlin.text.format

fun Long.toFileSize(): String {
    if (this <= 0) {
        return "0 B"
    }

    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (log10(this.toDouble()) / log10(1024.0)).toInt()

    return "${DecimalFormat("#,##0.#").format(this / 1024.0.pow(digitGroups.toDouble()))} ${units[digitGroups]}"
}

fun Long.toFormattedTime(): String {
    val minutes = TimeUnit.MILLISECONDS.toMinutes(this)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(this) % 60

    return String.format("%02d:%02d", minutes, seconds)
}

fun Long.toFormattedDate(): String {
    val instant = Instant.ofEpochMilli(this)
    val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
    return formatter.format(localDateTime)
}
