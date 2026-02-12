package com.example.offlinemusicplayer.util

import androidx.compose.foundation.magnifier
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import java.util.concurrent.TimeUnit

fun Long?.toTimeMmSs(): String {
    if (this == null) return "00:00"

    val minutes = TimeUnit.MILLISECONDS.toMinutes(this)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(this) -
        TimeUnit.MINUTES.toSeconds(minutes)
    return String.format("%02d:%02d", minutes, seconds)
}

fun Modifier.scrollMagnifier(
    sourceCenter: Density.() -> Offset,
    magnifierCenter: (Density.() -> Offset)? = null,
    size: DpSize = DpSize(100.dp, 100.dp),
    cornerRadius: Float = 100f,
    elevation: Float = 4f,
    visible: Boolean = true
): Modifier = composed {
    if (!visible) return@composed Modifier

    magnifier(
        sourceCenter = sourceCenter,
        magnifierCenter = magnifierCenter,
        size = size,
        cornerRadius = cornerRadius.dp,
        elevation = elevation.dp
    )
}
