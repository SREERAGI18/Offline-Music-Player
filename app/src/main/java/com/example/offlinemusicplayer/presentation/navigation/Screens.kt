package com.example.offlinemusicplayer.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screens {
    @Serializable
    data object SongList : Screens()
    @Serializable
    data object Search : Screens()

    @Serializable
    data object Playlist : Screens()
    @Serializable
    data class PlaylistDetail(val playlistId: Long) : Screens()
    @Serializable
    data object Home : Screens()
    @Serializable
    data object Main : Screens()

    @Serializable
    data object NowPlayingQueue : Screens()

    companion object {
        const val PLAYLIST_ID_KEY = "playlistId"

        fun fromRoute(route: String?): Screens? {
            val formattedRoute = route?.split(".")?.last()
            return when {
                formattedRoute == null -> null
                formattedRoute.contains("PlaylistDetail") -> {
                    val id = formattedRoute.substringAfter("PlaylistDetail/").toLongOrNull() ?: 0L
                    PlaylistDetail(id)
                }
                formattedRoute == "SongList" -> SongList
                formattedRoute == "Search" -> Search
                formattedRoute == "Playlist" -> Playlist
                formattedRoute == "Home" -> Home
                formattedRoute == "Main" -> Main
                formattedRoute == "NowPlayingQueue" -> NowPlayingQueue
                else -> null
            }
        }
    }
}