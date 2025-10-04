package com.example.offlinemusicplayer.presentation.songlist

import androidx.compose.runtime.Composable
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
        songs = songs
    )
}