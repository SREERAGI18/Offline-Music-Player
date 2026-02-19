package com.example.offlinemusicplayer.domain.enumclasses

enum class RepeatMode {
    ONE,
    ALL,
    OFF,
    ;

    fun nextRepeatMode(): RepeatMode =
        when (this) {
            ALL -> ONE
            ONE -> OFF
            OFF -> ALL
        }
}
