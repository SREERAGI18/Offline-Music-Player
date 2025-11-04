package com.example.offlinemusicplayer.presentation.songlist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.offlinemusicplayer.domain.enum_classes.SongOptions
import com.example.offlinemusicplayer.domain.model.Song
import com.example.offlinemusicplayer.presentation.dialogs.DeleteConfirmDialog
import com.example.offlinemusicplayer.presentation.dialogs.ProgressDialog
import com.example.offlinemusicplayer.presentation.dialogs.SongDetailDialog
import com.example.offlinemusicplayer.presentation.components.SongsList
import kotlinx.coroutines.launch
import com.example.offlinemusicplayer.presentation.dialogs.AddToPlaylistDialog

@Composable
fun SongListScreen() {
    val viewModel: SongListVM = hiltViewModel()
    val songs = viewModel.songs

    val currentMedia by viewModel.currentMedia.collectAsStateWithLifecycle()
    var currentMediaIndex by remember {
        mutableIntStateOf(-1)
    }

    val songListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(currentMedia) {
        currentMediaIndex = viewModel.getMediaIndex(currentMedia)
    }

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

    Box(modifier = Modifier.fillMaxSize()) {
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
            scrollState = songListState,
            currentPlayingIndex = currentMediaIndex,
            songs = songs,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        FloatingActionButton(
            onClick = {
                scope.launch {
                    if(currentMediaIndex != -1) {
                        songListState.animateScrollToItem(currentMediaIndex)
                    }
                }
            },
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp)
                .padding(bottom = 16.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(
                imageVector = Icons.Filled.GpsFixed,
                contentDescription = "Move to current playing song in list",
            )
        }
    }
}