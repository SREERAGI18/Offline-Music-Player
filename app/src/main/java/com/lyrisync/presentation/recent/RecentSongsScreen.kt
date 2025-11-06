package com.lyrisync.presentation.recent

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lyrisync.MainActivity
import com.lyrisync.domain.enum_classes.SongOptions
import com.lyrisync.domain.model.Song
import com.lyrisync.presentation.components.SongsList
import com.lyrisync.presentation.dialogs.AddToPlaylistDialog
import com.lyrisync.presentation.dialogs.DeleteConfirmDialog
import com.lyrisync.presentation.dialogs.SongDetailDialog

@Composable
fun RecentSongsScreen() {
    val context = LocalContext.current
    val mainActivity = context as? MainActivity

    val viewModel: RecentSongsVM = hiltViewModel()
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
    val intentSenderRequest by viewModel.intentSenderRequest.collectAsStateWithLifecycle()
    val playlists = viewModel.playlists
    val contentUriToDelete = viewModel.contentUriToDelete

    var showDeleteDialog by remember { mutableStateOf(false) }
    var songToDelete by remember { mutableStateOf<Song?>(null) }

    var showDetailsDialog by remember { mutableStateOf(false) }
    var songForDetails by remember { mutableStateOf<Song?>(null) }

    var showAddToPlaylistDialog by remember { mutableStateOf(false) }
    var songForPlaylist by remember { mutableStateOf<Song?>(null) }

    LaunchedEffect(intentSenderRequest) {
        intentSenderRequest?.let { request ->
            if(contentUriToDelete == null) return@let
            mainActivity?.launchRecoverableSecurityPermission(
                intentSenderRequest = request,
                onPermissionGranted = {
                    showDeleteDialog = true
                    viewModel.resetIntentSenderRequest()
                }
            )
        }
    }

//    if(deleteProgress) {
//        ProgressDialog(title = "Deleting...")
//    }

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
                    viewModel.checkIfSongCanBeDeleted(song, context)
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
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    )
}