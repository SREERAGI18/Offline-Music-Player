package com.example.offlinemusicplayer.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.offlinemusicplayer.domain.model.Playlist

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val songIds: String,
) {
    fun toPlaylist(): Playlist {
        val songIdList =
            if (songIds.isBlank()) {
                emptyList()
            } else {
                songIds.split(",").mapNotNull { it.toLongOrNull() }
            }
        return Playlist(
            id = id,
            name = name,
            songIds = songIdList,
        )
    }

    companion object {
        const val RECENTLY_PLAYED_PLAYLIST_NAME = "Recently Played"
        const val RECENTLY_PLAYED_PLAYLIST_ID = 1L
        const val MOST_PLAYED_PLAYLIST_NAME = "Most Played"
        const val MOST_PLAYED_PLAYLIST_ID = 2L
        const val FAVORITES_NAME = "Favorites"
        const val FAVORITES_ID = 3L

        val DEFAULT_PLAYLIST_MAP =
            mapOf<Long, String>(
                RECENTLY_PLAYED_PLAYLIST_ID to RECENTLY_PLAYED_PLAYLIST_NAME,
                MOST_PLAYED_PLAYLIST_ID to MOST_PLAYED_PLAYLIST_NAME,
                FAVORITES_ID to FAVORITES_NAME,
            )
    }
}
