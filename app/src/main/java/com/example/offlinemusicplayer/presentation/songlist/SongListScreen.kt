package com.example.offlinemusicplayer.presentation.songlist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.session.MediaController
import com.example.offlinemusicplayer.domain.model.Song

@Composable
fun SongListScreen(
    onSongClick: (Song) -> Unit,
    controller: MediaController?
) {
    val viewModel: SongListVM = hiltViewModel()
    val songs by viewModel.songs.collectAsStateWithLifecycle()

    Column {
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(songs) { song ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onSongClick(song)
                        }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${song.title} - ${song.artist}")
                    IconButton(onClick = {
                        if (controller?.isPlaying == true) {
                            controller.pause()
                        } else {
                            controller?.play()
                        }
                    }) {
                        Icon(
                            imageVector = if (controller?.isPlaying == true)
                                Icons.Filled.Edit else Icons.Default.PlayArrow,
                            contentDescription = "Play/Pause"
                        )
                    }
                }
            }
        }

//        Button(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp),
//            onClick = onGoToPlaylists
//        ) {
//            Text("Go to Playlists")
//        }
    }
}