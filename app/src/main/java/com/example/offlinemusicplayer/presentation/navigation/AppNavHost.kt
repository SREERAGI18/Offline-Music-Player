package com.example.offlinemusicplayer.presentation.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.MediaItem
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.offlinemusicplayer.player.rememberMusicController
import com.example.offlinemusicplayer.presentation.main.MainScreen
import com.example.offlinemusicplayer.presentation.playlist.PlaylistScreen
import com.example.offlinemusicplayer.presentation.search.SearchScreen
import com.example.offlinemusicplayer.presentation.songlist.SongListScreen
import java.io.File

@Composable
fun AppNavHost(
    navController: NavHostController,
    query: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val controller = rememberMusicController(context)

    NavHost(
        navController = navController,
        startDestination = Screens.Main.route,
        modifier = modifier
    ) {
        composable(Screens.Main.route) {
            MainScreen(controller)
        }
        composable(Screens.SongList.route) {
            SongListScreen(
                onSongClick = { /* play via service */ },
            )
        }

        composable(Screens.Search.route) {
            SearchScreen(
                onSongClick = { song ->
                    // Prepare and play selected song
                    controller?.setMediaItem(
                        MediaItem.fromUri(
                            Uri.fromFile(
                                File(song.path)
                            )
                        )
                    )
                    controller?.prepare()
                    controller?.playWhenReady = true
                },
                query = query
            )
        }

        composable(Screens.Playlist.route) {
            PlaylistScreen(
                onCreatePlaylist = { /* insert into DB */ }
            )
        }
    }
}
