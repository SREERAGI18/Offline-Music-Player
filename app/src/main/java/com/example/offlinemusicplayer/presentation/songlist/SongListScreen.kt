package com.example.offlinemusicplayer.presentation.songlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.offlinemusicplayer.presentation.components.SongsList
import kotlinx.coroutines.launch

@Composable
fun SongListScreen() {
    val viewModel: SongListVM = hiltViewModel()
    val songs = viewModel.songs

    val currentMediaIndex by viewModel.currentMediaIndex.collectAsStateWithLifecycle()

    val songListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {

        SongsList(
            onSongClick = { song, index ->
                viewModel.playSong(index)
            },
            songs = songs,
            scrollState = songListState,
            currentPlayingIndex = currentMediaIndex,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        FloatingActionButton(
            onClick = {
                scope.launch {
                    currentMediaIndex?.let {
                        songListState.animateScrollToItem(it)
                    }
                }
            },
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp)
                .padding(bottom = 16.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(
                imageVector = Icons.Filled.GpsFixed,
                contentDescription = "Move to current playing song in list",
            )
        }
    }
}