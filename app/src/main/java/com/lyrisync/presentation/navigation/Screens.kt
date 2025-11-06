package com.lyrisync.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

@Serializable
sealed class Screens {
    @Serializable
    data object SongList : Screens()
    @Serializable
    data object Search : Screens()

    @Serializable
    data object Profile : Screens()

    @Serializable
    data object Settings : Screens()

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

    data class MenuItem(
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
                formattedRoute == "Profile" -> Profile
                formattedRoute == "Settings" -> Settings
                else -> null
            }
        }

        val bottomMenuItems = listOf(
            MenuItem(
                screen = Main,
                label = "Music",
                imageVector = Icons.Default.LibraryMusic
            ),
            MenuItem(
                screen = Search,
                label = "Search",
                imageVector = Icons.Default.Search
            )
        )

        val drawerMenuItems = listOf(
            MenuItem(
                screen = Settings,
                label = "Settings",
                imageVector = Icons.Default.Settings
            ),
            MenuItem(
                screen = Profile,
                label = "Profile",
                imageVector = Icons.Default.AccountCircle
            ),
        )

    }
}