package com.example.offlinemusicplayer.presentation.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.offlinemusicplayer.data.local.entity.SongsEntity
import com.example.offlinemusicplayer.presentation.components.SongsList

@Composable
fun SearchScreen(
    query: String
) {
    val viewModel: SearchVM = hiltViewModel()
    val songs = viewModel.songs.collectAsLazyPagingItems()

    LaunchedEffect(query) {
        viewModel.updateSearchQuery(query)
    }

    LaunchedEffect(songs.itemSnapshotList.items) {
        if (songs.itemSnapshotList.isNotEmpty()) {
            viewModel.setPlayerList(songs.itemSnapshotList.items)
        }
    }

    SongsList(
        onSongClick = { song, index ->
            viewModel.playSong(index)
        },
        songs = songs
    )
}