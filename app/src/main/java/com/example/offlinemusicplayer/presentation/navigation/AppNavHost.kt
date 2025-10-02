package com.example.offlinemusicplayer.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.offlinemusicplayer.presentation.main.MainScreen
import com.example.offlinemusicplayer.presentation.playlist.PlaylistScreen
import com.example.offlinemusicplayer.presentation.playlist.PlaylistViewModel
import com.example.offlinemusicplayer.presentation.songlist.SongListScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screens.Main.route,
        modifier = modifier
    ) {
        composable(Screens.Main.route) {
            MainScreen()
        }
        composable(Screens.SongList.route) {
            SongListScreen(
                onSongClick = { /* play via service */ },
                controller = null
            )
        }

        composable(Screens.Playlist.route) {
            PlaylistScreen(
                onCreatePlaylist = { /* insert into DB */ }
            )
        }
    }
}
