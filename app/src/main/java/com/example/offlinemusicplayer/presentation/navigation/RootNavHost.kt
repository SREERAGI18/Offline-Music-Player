package com.example.offlinemusicplayer.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.offlinemusicplayer.presentation.main.MainScreen
import com.example.offlinemusicplayer.presentation.now_playing_queue.NowPlayingQueueScreen
import com.example.offlinemusicplayer.presentation.playlist_detail.PlaylistDetailScreen

@Composable
fun RootNavHost(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screens.Main
    ) {
        composable<Screens.PlaylistDetail> { backStackEntry ->
            PlaylistDetailScreen(
                onBackPress = {
                    navController.popBackStack()
                }
            )
        }

        composable<Screens.NowPlayingQueue> { backStackEntry ->
            NowPlayingQueueScreen(
                onBackPress = {
                    navController.popBackStack()
                }
            )
        }

        composable<Screens.Main> {
            MainScreen(
                onNavigate = {
                    navController.navigate(it)
                }
            )
        }
    }
}