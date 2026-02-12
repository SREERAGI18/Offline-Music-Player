package com.example.offlinemusicplayer.presentation.providers

import androidx.compose.material3.BottomAppBarScrollBehavior
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.staticCompositionLocalOf

@OptIn(ExperimentalMaterial3Api::class)
val LocalScrollBehavior = staticCompositionLocalOf<TopAppBarScrollBehavior> {
    error("No ScrollBehavior provided")
}

@OptIn(ExperimentalMaterial3Api::class)
val LocalBottomScrollBehavior = staticCompositionLocalOf<BottomAppBarScrollBehavior> {
    error("No BottomScrollBehavior provided")
}
