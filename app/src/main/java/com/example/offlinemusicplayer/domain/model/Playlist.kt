package com.example.offlinemusicplayer.domain.model

data class Playlist(
    val id: Long = 0,
    val name: String,
    val songIds: List<Long>
)
