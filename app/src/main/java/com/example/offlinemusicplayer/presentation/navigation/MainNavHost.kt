package com.example.offlinemusicplayer.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.offlinemusicplayer.presentation.home.HomeScreen
import com.example.offlinemusicplayer.presentation.playlist.PlaylistScreen
import com.example.offlinemusicplayer.presentation.playlist_detail.PlaylistDetailScreen
import com.example.offlinemusicplayer.presentation.search.SearchScreen
import com.example.offlinemusicplayer.presentation.songlist.SongListScreen

@Composable
fun MainNavHost(
    navController: NavHostController,
    rootNavController: NavHostController,
    query: String,
    onNavigate:(Screens) -> Unit,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screens.Home,
        modifier = modifier
    ) {
        composable<Screens.Home> {
            HomeScreen(
                navController = navController,
                rootNavController = rootNavController,
                onNavigate = onNavigate
            )
        }

        composable<Screens.Search> {
            SearchScreen(
                query = query
            )
        }
    }
}
