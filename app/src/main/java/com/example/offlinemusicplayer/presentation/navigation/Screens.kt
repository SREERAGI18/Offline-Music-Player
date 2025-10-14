package com.example.offlinemusicplayer.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screens {
    @Serializable
    object SongList : Screens()
    @Serializable
    object Search : Screens()

    @Serializable
    object Playlist : Screens()
    @Serializable
    data class PlaylistDetail(val playlistId: Long) : Screens()
    @Serializable
    object Home : Screens()
    @Serializable
    object Main : Screens()

    companion object {
        const val PLAYLIST_ID_KEY = "playlistId"
    }
}
