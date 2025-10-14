package com.example.offlinemusicplayer.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.offlinemusicplayer.presentation.home.HomeScreen
import com.example.offlinemusicplayer.presentation.playlist.PlaylistScreen
import com.example.offlinemusicplayer.presentation.search.SearchScreen
import com.example.offlinemusicplayer.presentation.songlist.SongListScreen

@Composable
fun MainNavHost(
    navController: NavHostController,
    rootNavController: NavHostController,
    query: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screens.Home,
        modifier = modifier
    ) {
        composable<Screens.Home> {
            HomeScreen()
        }
        composable<Screens.SongList> {
            SongListScreen()
        }

        composable<Screens.Search> {
            SearchScreen(
                query = query
            )
        }

        composable<Screens.Playlist> {
            PlaylistScreen(
                onPlaylistClicked = { playlist ->
                    rootNavController.navigate(
                        route = Screens.PlaylistDetail(playlist.id)
                    )
                }
            )
        }
    }
}
