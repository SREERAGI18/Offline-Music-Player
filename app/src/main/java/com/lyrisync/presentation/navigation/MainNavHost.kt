package com.lyrisync.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.lyrisync.presentation.music.MusicScreen
import com.lyrisync.presentation.now_playing_queue.NowPlayingQueueScreen
import com.lyrisync.presentation.playlist_detail.PlaylistDetailScreen

@Composable
fun MainNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screens.Music,
        modifier = modifier
    ) {
        composable<Screens.Music> {
            MusicScreen(
                navController = navController
            )
        }

        composable<Screens.PlaylistDetail> { backStackEntry ->
            PlaylistDetailScreen(
                onBackPress = {
                    navController.popBackStack()
                }
            )
        }
    }
}
