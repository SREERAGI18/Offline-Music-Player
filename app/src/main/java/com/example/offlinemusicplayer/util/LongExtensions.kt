package com.example.offlinemusicplayer.util

import com.example.offlinemusicplayer.util.Constants.BYTES_PER_KB_D
import com.example.offlinemusicplayer.util.Constants.SECONDS_PER_MINUTE
import java.text.DecimalFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.log10
import kotlin.math.pow
import kotlin.text.format

fun Long.toFileSize(): String {
    if (this <= 0) {
        return "0 B"
    }

    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (log10(this.toDouble()) / log10(BYTES_PER_KB_D)).toInt()

    return "${DecimalFormat("#,##0.#").format(this / BYTES_PER_KB_D.pow(digitGroups.toDouble()))} ${units[digitGroups]}"
}

fun Long.toFormattedTime(): String {
    val minutes = TimeUnit.MILLISECONDS.toMinutes(this)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(this) % SECONDS_PER_MINUTE

    return String.format(Locale.US, "%02d:%02d", minutes, seconds)
}

fun Long.toFormattedDate(): String {
    val instant = Instant.ofEpochMilli(this)
    val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
    return formatter.format(localDateTime)
}
