package com.example.offlinemusicplayer.presentation.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.offlinemusicplayer.presentation.navigation.MainNavHost
import com.example.offlinemusicplayer.presentation.navigation.Screens
import com.example.offlinemusicplayer.presentation.now_playing.NowPlayingBar
import com.example.offlinemusicplayer.presentation.now_playing_detail.NowPlayingDetail
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigate:(Screens) -> Unit,
) {
    val viewModel = hiltViewModel<MainVM>()
    val currentSong by viewModel.currentMedia.collectAsStateWithLifecycle()

    val navController = rememberNavController()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isSheetVisible by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    if (isSheetVisible && currentSong != null) {
        ModalBottomSheet(
            onDismissRequest = {
                scope.launch {
                    sheetState.hide()
                    isSheetVisible = false
                }
            },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            dragHandle = null,
            modifier = Modifier.fillMaxSize()
        ) {
            NowPlayingDetail(
                viewModel = viewModel,
                onCollapse = {
                    scope.launch {
                        sheetState.hide()
                        isSheetVisible = false
                    }
                },
                onNavigate = {
                    navController.navigate(it)
                }
            )
        }
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        MainNavHost(
            navController = navController,
        )
        if (currentSong != null && !isSheetVisible) {
            NowPlayingBar(
                viewModel = viewModel,
                onClick = {
                    scope.launch {
                        isSheetVisible = true
                        sheetState.expand()
                    }
                },
            )
        }
    }
}