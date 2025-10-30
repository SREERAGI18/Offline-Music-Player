package com.example.offlinemusicplayer.presentation.songlist

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.offlinemusicplayer.domain.enum_classes.SongOptions
import com.example.offlinemusicplayer.presentation.components.SongsList

@Composable
fun SongListScreen() {
    val viewModel: SongListVM = hiltViewModel()
    val songs = viewModel.songs.collectAsLazyPagingItems()

    SongsList(
        onSongClick = { song, index ->
            viewModel.playSong(index)
        },
        onOptionSelected = { song, option ->
            when(option) {
                SongOptions.PlayNext -> {
                    viewModel.playNext(song)
                }
                SongOptions.AddToQueue -> {
                    viewModel.addToQueue(song)
                }
                SongOptions.EditSongInfo -> {

                }
                SongOptions.Delete -> {

                }
                SongOptions.Details -> {

                }
            }
        },
        songs = songs,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}