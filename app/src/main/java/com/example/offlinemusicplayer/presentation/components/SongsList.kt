package com.example.offlinemusicplayer.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.example.offlinemusicplayer.domain.enum_classes.SongOptions
import com.example.offlinemusicplayer.domain.model.Song

@Composable
fun SongsList(
    modifier: Modifier = Modifier,
    songs: LazyPagingItems<Song>,
    onSongClick: (Song, Int) -> Unit,
    onOptionSelected: ((Song, SongOptions) -> Unit)? = null
) {

    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(
            count = songs.itemCount,
            key = songs.itemKey { it.id }
        ) { index ->
            val song = songs[index] ?: return@items
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                SongItem(
                    song = song,
                    onSongClick = {
                        onSongClick(song, index)
                    },
                    onOptionSelected = {
                        onOptionSelected?.invoke(song, it)
                    }
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun SongsList(
    modifier: Modifier = Modifier,
    songs: List<Song>,
    onSongClick: (Song, Int) -> Unit,
    onOptionSelected: ((Song, SongOptions) -> Unit)? = null
) {

    LazyColumn(modifier = modifier.fillMaxSize()) {
        itemsIndexed(songs) { index, song, ->

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                SongItem(
                    song = song,
                    onSongClick = {
                        onSongClick(song, index)
                    },
                    onOptionSelected = {
                        onOptionSelected?.invoke(song, it)
                    }
                )
                HorizontalDivider()
            }
        }
    }
}