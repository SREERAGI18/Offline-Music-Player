package com.example.offlinemusicplayer.presentation.songlist

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.offlinemusicplayer.domain.enum_classes.SongOptions
import com.example.offlinemusicplayer.domain.model.Song
import com.example.offlinemusicplayer.presentation.dialogs.DeleteConfirmDialog
import com.example.offlinemusicplayer.presentation.dialogs.ProgressDialog
import com.example.offlinemusicplayer.presentation.dialogs.SongDetailDialog
import com.example.offlinemusicplayer.presentation.components.SongsList
import com.example.offlinemusicplayer.presentation.dialogs.AddToPlaylistDialog

@Composable
fun SongListScreen() {
    val viewModel: SongListVM = hiltViewModel()
    val songs = viewModel.songs.collectAsLazyPagingItems()
    val deleteProgress by viewModel.deleteProgress.collectAsStateWithLifecycle()
    val playlists = viewModel.playlists

    var showDeleteDialog by remember { mutableStateOf(false) }
    var songToDelete by remember { mutableStateOf<Song?>(null) }

    var showDetailsDialog by remember { mutableStateOf(false) }
    var songForDetails by remember { mutableStateOf<Song?>(null) }

    var showAddToPlaylistDialog by remember { mutableStateOf(false) }
    var songForPlaylist by remember { mutableStateOf<Song?>(null) }

    if(deleteProgress) {
        ProgressDialog(title = "Deleting...")
    }

    if (showAddToPlaylistDialog) {
        songForPlaylist?.let { song ->
            AddToPlaylistDialog(
                playlists = playlists.filter { !it.songIds.contains(song.id) },
                onPlaylistSelected = { playlist ->
                    viewModel.addToPlaylist(song, playlist)
                    showAddToPlaylistDialog = false
                },
                onDismiss = { showAddToPlaylistDialog = false }
            )
        }
    }

    if(showDeleteDialog) {
        DeleteConfirmDialog(
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                songToDelete?.let { song ->
                    viewModel.deleteSongFile(song)
                }
                showDeleteDialog = false
            },
            description = "\"${songToDelete?.title}\" will be permanently deleted from storage."
        )
    }

    if(showDetailsDialog) {
        songForDetails?.let { song ->
            SongDetailDialog(
                song = song,
                onDismiss = {
                    showDetailsDialog = false
                }
            )
        }
    }

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
                SongOptions.AddToPlaylist -> {
                    songForPlaylist = song
                    showAddToPlaylistDialog = true
                }
//                SongOptions.EditSongInfo -> {
//
//                }
                SongOptions.Delete -> {
                    songToDelete = song
                    showDeleteDialog = true
                }
                SongOptions.Details -> {
                    songForDetails = song
                    showDetailsDialog = true
                }
            }
        },
        songs = songs,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}