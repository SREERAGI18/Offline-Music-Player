package com.example.offlinemusicplayer.presentation.main

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.offlinemusicplayer.presentation.navigation.MainNavHost

@Composable
fun MainScreen(
    navController: NavHostController
) {
    MainNavHost(
        navController = navController,
    )
}