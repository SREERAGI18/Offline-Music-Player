package com.example.offlinemusicplayer.domain.model

import com.example.offlinemusicplayer.data.local.entity.PlaylistEntity

data class Playlist(
    val id: Long = 0,
    val name: String,
    val songIds: List<Long>,
) {
    fun toPlaylistEntity(): PlaylistEntity =
        PlaylistEntity(
            id = id,
            name = name,
            songIds = songIds.joinToString(","),
        )
}
