package com.lyrisync.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.lyrisync.presentation.main.MainScreen
import com.lyrisync.presentation.now_playing_queue.NowPlayingQueueScreen
import com.lyrisync.presentation.search.SearchScreen

@Composable
fun RootNavHost(
    navController: NavHostController,
    mainNavController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screens.Main,
        modifier = modifier
    ) {

        composable<Screens.Main> {
            MainScreen(mainNavController)
        }

        composable<Screens.Shazam> {
            Box(modifier = Modifier.fillMaxSize())
        }

        composable<Screens.Search> {
            SearchScreen()
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