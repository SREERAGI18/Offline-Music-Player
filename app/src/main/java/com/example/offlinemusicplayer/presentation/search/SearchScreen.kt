package com.example.offlinemusicplayer.presentation.search

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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

    SongsList(
        onSongClick = { song, index ->
            viewModel.playSong(index)
        },
        songs = songs,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}