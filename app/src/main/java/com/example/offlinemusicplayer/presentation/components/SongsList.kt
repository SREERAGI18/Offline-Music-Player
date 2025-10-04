package com.example.offlinemusicplayer.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.example.offlinemusicplayer.data.local.entity.SongsEntity

@Composable
fun SongsList(
    songs: LazyPagingItems<SongsEntity>,
    onSongClick: (SongsEntity) -> Unit,
) {

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(
            count = songs.itemCount,
            key = songs.itemKey { it.id }
        ) { index ->
            val song = songs[index] ?: return@items
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                SongItem(song, onSongClick)
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}