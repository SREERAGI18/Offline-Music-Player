package com.example.offlinemusicplayer.presentation.playlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.offlinemusicplayer.domain.model.Playlist
import com.example.offlinemusicplayer.presentation.components.PlaylistItem
import com.example.offlinemusicplayer.presentation.dialogs.CreatePlaylistDialog
import com.example.offlinemusicplayer.presentation.dialogs.SongSelectionDialog

@Composable
fun PlaylistScreen(
    onPlaylistClicked: (Playlist) -> Unit
) {
    val viewModel = hiltViewModel<PlaylistVM>()
    val playlists by viewModel.playlists.collectAsStateWithLifecycle()
    val currentMedia by viewModel.currentMedia.collectAsStateWithLifecycle()

    var showSongSelection by remember {
        mutableStateOf(false)
    }

    var showCreatePlaylistDialog by remember {
        mutableStateOf(false)
    }

    var playlistTitle by remember {
        mutableStateOf("")
    }

    if(showSongSelection) {
        SongSelectionDialog(
            songs = viewModel.songs,
            title = playlistTitle,
            onSubmit = { selectedSongs ->
                viewModel.addPlaylist(playlistTitle, selectedSongs)
                showSongSelection = false
            },
            onCancel = {
                showSongSelection = false
            }
        )
    }

    if(showCreatePlaylistDialog) {
        CreatePlaylistDialog(
            onDismiss = {
                showCreatePlaylistDialog = false
            },
            onCreate = {
                playlistTitle = it
                showCreatePlaylistDialog = false
                showSongSelection = true
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 140.dp),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(playlists, key = { it.id }) { playlist ->
                PlaylistItem(
                    playlist = playlist,
                    onClick = {
                        onPlaylistClicked(playlist)
                    }
                )
            }
        }

        FloatingActionButton(
            onClick = {
                showCreatePlaylistDialog = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp)
                .padding(bottom = 16.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add Playlist",
            )
        }
    }
}