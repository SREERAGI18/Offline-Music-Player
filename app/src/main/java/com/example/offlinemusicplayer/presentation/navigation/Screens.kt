package com.example.offlinemusicplayer.presentation.navigation

sealed class Screens(val route: String) {
    object SongList : Screens("songs")
    object Search : Screens("search")
    object Playlist : Screens("playlists")
    object Main : Screens("main")
}
