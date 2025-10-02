package com.example.offlinemusicplayer.presentation.playlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.offlinemusicplayer.domain.model.Playlist

@Composable
fun PlaylistScreen(
    onCreatePlaylist: (String) -> Unit
) {
    val viewModel = hiltViewModel<PlaylistViewModel>()
    val playlists by viewModel.playlists.collectAsStateWithLifecycle()

    Column {
        LazyColumn {
            items(playlists) { playlist ->
                Text(
                    text = playlist.name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        Button(
            modifier = Modifier.padding(16.dp),
            onClick = { onCreatePlaylist("New Playlist") }
        ) {
            Text("Create Playlist")
        }
    }
}