package com.example.offlinemusicplayer.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.offlinemusicplayer.domain.model.Playlist

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val songIds: String
) {
    fun toPlaylist(): Playlist {
        val songIdList = if (songIds.isBlank()) {
            emptyList()
        } else {
            songIds.split(",").mapNotNull { it.toLongOrNull() }
        }
        return Playlist(
            id = id,
            name = name,
            songIds = songIdList
        )
    }
}
