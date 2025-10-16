package com.example.offlinemusicplayer.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.offlinemusicplayer.presentation.home.HomeScreen
import com.example.offlinemusicplayer.presentation.search.SearchScreen

@Composable
fun MainNavHost(
    navController: NavHostController,
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
