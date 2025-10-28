package com.example.offlinemusicplayer.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.offlinemusicplayer.presentation.main.MainScreen
import com.example.offlinemusicplayer.presentation.search.SearchScreen

@Composable
fun RootNavHost(
    navController: NavHostController,
    modifier: Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screens.Main,
        modifier = modifier
    ) {

        composable<Screens.Main> {
            MainScreen(
                onNavigate = {
                    navController.navigate(it)
                },
            )
        }

        composable<Screens.Shazam> {
            Box(modifier = Modifier.fillMaxSize())
        }

        composable<Screens.Search> {
            SearchScreen()
        }
    }
}