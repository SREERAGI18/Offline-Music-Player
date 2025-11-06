package com.lyrisync.presentation.playlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lyrisync.domain.enum_classes.PlaylistOptions
import com.lyrisync.domain.model.Playlist
import com.lyrisync.presentation.components.PlaylistItem
import com.lyrisync.presentation.dialogs.CreatePlaylistDialog
import com.lyrisync.presentation.dialogs.DeleteConfirmDialog
import com.lyrisync.presentation.dialogs.SongSelectionDialog

@Composable
fun PlaylistScreen(
    onPlaylistClicked: (Playlist) -> Unit
) {
    val context = LocalContext.current
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

    var isCreatePlaylist by remember {
        mutableStateOf(false)
    }

    var showDeleteDialog by remember {
        mutableStateOf(false)
    }

    if(showDeleteDialog) {
        DeleteConfirmDialog(
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                viewModel.deletePlaylist()
                showDeleteDialog = false
            },
            description = "\"${viewModel.playlistToModify?.name}\" will be permanently deleted."
        )
    }

    if(showSongSelection) {
        SongSelectionDialog(
            songs = viewModel.songs,
            title = playlistTitle,
            selectedSongIds = viewModel.playlistToModify?.songIds,
            onSubmit = { selectedSongs ->
                if(viewModel.playlistToModify != null) {
                    viewModel.updatePlaylistContent(selectedSongs)
                } else {
                    viewModel.addPlaylist(playlistTitle, selectedSongs)
                }
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
            initialName = playlistTitle,
            isCreatePlaylist = isCreatePlaylist,
            onCreate = {
                if(isCreatePlaylist) {
                    playlistTitle = it
                    showCreatePlaylistDialog = false
                    showSongSelection = true
                } else {
                    viewModel.updatePlaylistName(it)
                    showCreatePlaylistDialog = false
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(
                items = playlists,
                key = { it.id }
            ) { playlist ->
                PlaylistItem(
                    playlist = playlist,
                    onClick = {
                        onPlaylistClicked(playlist)
                    },
                    onOptionSelected = { option ->
                        when(option) {
                            PlaylistOptions.Play -> {
                                viewModel.playAllSongsOfPlaylist(playlist)
                            }
                            PlaylistOptions.AddToQueue -> {
                                viewModel.addAllSongsToQueue(
                                    context = context,
                                    playlist = playlist
                                )
                            }
                            PlaylistOptions.EditName -> {
                                viewModel.playlistToModify = playlist
                                playlistTitle = playlist.name
                                isCreatePlaylist = false
                                showCreatePlaylistDialog = true
                            }
                            PlaylistOptions.EditContent -> {
                                viewModel.playlistToModify = playlist
                                showSongSelection = true
                            }
                            PlaylistOptions.Delete -> {
                                viewModel.playlistToModify = playlist
                                showDeleteDialog = true
                            }
                        }
                    }
                )
            }
        }

        FloatingActionButton(
            onClick = {
                playlistTitle = ""
                isCreatePlaylist = true
                showCreatePlaylistDialog = true
            },
            modifier = Modifier.align(Alignment.BottomEnd),
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