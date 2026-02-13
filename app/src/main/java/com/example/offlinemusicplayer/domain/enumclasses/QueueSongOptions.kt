package com.example.offlinemusicplayer.domain.enumclasses

enum class QueueSongOptions(override val displayName: String) : Options {
    Play("Play"),
    RemoveFromQueue("Remove from queue")
}
