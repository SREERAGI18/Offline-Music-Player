package com.example.offlinemusicplayer.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.example.offlinemusicplayer.data.local.entity.SongsEntity
import com.example.offlinemusicplayer.domain.model.Song

@Composable
fun SongsList(
    modifier: Modifier = Modifier,
    songs: LazyPagingItems<Song>,
    onSongClick: (Song, Int) -> Unit,
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
                    }
                )
                HorizontalDivider()
            }
        }
    }
}