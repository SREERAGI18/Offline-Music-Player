package com.example.offlinemusicplayer.domain.enum_classes

enum class QueueSongOptions(override val displayName: String): Options {
    Play("Play"),
    RemoveFromQueue("Remove from queue")
}