package com.example.offlinemusicplayer.domain.enum_classes

enum class RepeatMode {
    ONE, ALL, OFF;

    fun nextRepeatMode(): RepeatMode {
        return when (this) {
            ALL -> ONE
            ONE -> OFF
            OFF -> ALL
        }
    }
}