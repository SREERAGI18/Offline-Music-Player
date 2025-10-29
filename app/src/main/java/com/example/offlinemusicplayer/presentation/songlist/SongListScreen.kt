package com.example.offlinemusicplayer.presentation.songlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.offlinemusicplayer.presentation.components.SongsList

@Composable
fun SongListScreen() {
    val viewModel: SongListVM = hiltViewModel()
    val songs = viewModel.songs.collectAsLazyPagingItems()

    SongsList(
        onSongClick = { song, index ->
            viewModel.playSong(index)
        },
        songs = songs,
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.background
            )
            .padding(horizontal = 16.dp)
    )
}