package com.example.offlinemusicplayer.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.example.offlinemusicplayer.domain.enumclasses.SongOptions
import com.example.offlinemusicplayer.domain.model.Song

@Composable
fun SongsList(
    songs: LazyPagingItems<Song>,
    scrollState: LazyListState,
    onSongClick: (Song, Int) -> Unit,
    modifier: Modifier = Modifier,
    currentPlayingIndex: Int? = -1,
    onOptionSelected: ((Song, SongOptions) -> Unit)? = null,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = scrollState,
    ) {
        items(
            count = songs.itemCount,
            key = songs.itemKey { it.id },
        ) { index ->
            val song = songs[index] ?: return@items
            val isPlaying = currentPlayingIndex == index
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                SongItem(
                    song = song,
                    onSongClick = {
                        onSongClick(song, index)
                    },
                    onOptionSelected = {
                        onOptionSelected?.invoke(song, it)
                    },
                    isPlaying = isPlaying,
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun SongsList(
    songs: List<Song>,
    scrollState: LazyListState,
    onSongClick: (Song, Int) -> Unit,
    modifier: Modifier = Modifier,
    currentPlayingIndex: Int? = -1,
    onOptionSelected: ((Song, SongOptions) -> Unit)? = null,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = scrollState,
    ) {
        itemsIndexed(songs) { index, song ->

            val isPlaying = currentPlayingIndex == index

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                SongItem(
                    song = song,
                    onSongClick = {
                        onSongClick(song, index)
                    },
                    onOptionSelected = {
                        onOptionSelected?.invoke(song, it)
                    },
                    isPlaying = isPlaying,
                )
                HorizontalDivider()
            }
        }
    }
}
