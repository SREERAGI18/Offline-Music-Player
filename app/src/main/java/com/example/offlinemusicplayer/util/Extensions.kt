package com.example.offlinemusicplayer.util

import java.util.concurrent.TimeUnit

fun Long?.toTimeMmSs(): String {
    if(this == null) return "00:00"

    val minutes = TimeUnit.MILLISECONDS.toMinutes(this)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(this) -
            TimeUnit.MINUTES.toSeconds(minutes)
    return String.format("%02d:%02d", minutes, seconds)
}
