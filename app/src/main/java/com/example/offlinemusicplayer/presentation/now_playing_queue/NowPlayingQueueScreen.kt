package com.example.offlinemusicplayer.presentation.now_playing_queue

import QueueSongsList
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.offlinemusicplayer.domain.enum_classes.QueueSongOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NowPlayingQueueScreen(
    onBackPress: () -> Unit
) {
    val viewModel: NowPlayingQueueVM = hiltViewModel()

    val currentMedia by viewModel.currentMedia.collectAsStateWithLifecycle()

    LaunchedEffect(currentMedia) {
        val playingSongId = currentMedia?.id

        viewModel.updateCurrentPlaying(playingSongId)
    }

    LaunchedEffect(Unit) {
        viewModel.getCurrentMediaList()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Now playing",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackPress,
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = MaterialTheme.colorScheme.onBackground
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { paddingValues ->
        QueueSongsList(
            songs = viewModel.currentQueue,
            onSongClick = { song, index ->
                viewModel.playSong(index)
            },
            onSongMoved = { from, to ->
                viewModel.moveSong(from, to)
            },
            onOptionSelected = { index, option ->
                when(option) {
                    QueueSongOptions.Play -> {
                        viewModel.playSong(index)
                    }
                    QueueSongOptions.RemoveFromQueue -> {
                        viewModel.removeSong(index)
                    }
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        )
    }
}