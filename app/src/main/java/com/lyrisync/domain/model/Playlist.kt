package com.lyrisync.domain.model

import com.lyrisync.data.local.entity.PlaylistEntity

data class Playlist(
    val id: Long = 0,
    val name: String,
    val songIds: List<Long>
) {
    fun toPlaylistEntity(): PlaylistEntity {
        return PlaylistEntity(
            id = id,
            name = name,
            songIds = songIds.joinToString(",")
        )
    }
}
