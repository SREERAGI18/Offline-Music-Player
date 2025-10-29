package com.example.offlinemusicplayer.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
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
    data object Music : Screens()
    @Serializable
    data object Main : Screens()

    @Serializable
    data object Shazam : Screens()

    @Serializable
    data object NowPlayingQueue : Screens()

    data class BottomMenuItem(
        val screen: Screens,
        val label: String,
        val imageVector: ImageVector
    )

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
                formattedRoute == "Music" -> Music
                formattedRoute == "Main" -> Main
                formattedRoute == "Shazam" -> Shazam
                formattedRoute == "NowPlayingQueue" -> NowPlayingQueue
                else -> null
            }
        }

        val bottomMenuItems = listOf(
            BottomMenuItem(
                screen = Main,
                label = "Music",
                imageVector = Icons.Default.LibraryMusic
            ),
            BottomMenuItem(
                screen = Shazam,
                label = "Shazam",
                imageVector = Icons.Default.Mic
            ),
            BottomMenuItem(
                screen = Search,
                label = "Search",
                imageVector = Icons.Default.Search
            )
        )

    }
}