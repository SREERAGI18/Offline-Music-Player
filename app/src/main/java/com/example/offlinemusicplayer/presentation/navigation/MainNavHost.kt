package com.example.offlinemusicplayer.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.offlinemusicplayer.presentation.home.HomeScreen
import com.example.offlinemusicplayer.presentation.now_playing_queue.NowPlayingQueueScreen
import com.example.offlinemusicplayer.presentation.playlist_detail.PlaylistDetailScreen
import com.example.offlinemusicplayer.presentation.search.SearchScreen

@Composable
fun MainNavHost(
    navController: NavHostController,
    query: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screens.Home,
        modifier = modifier
    ) {
        composable<Screens.Home> {
            HomeScreen(
                navController = navController
            )
        }

        composable<Screens.Search> {
            SearchScreen(
                query = query
            )
        }

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
    }
}
