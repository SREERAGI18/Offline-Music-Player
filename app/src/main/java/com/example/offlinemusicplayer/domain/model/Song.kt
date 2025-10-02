package com.example.offlinemusicplayer.domain.model

data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val path: String,
    val duration: Long
)
