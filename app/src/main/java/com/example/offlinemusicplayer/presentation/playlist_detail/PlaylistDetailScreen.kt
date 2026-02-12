package com.example.offlinemusicplayer.presentation.playlist_detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.offlinemusicplayer.data.local.entity.PlaylistEntity
import com.example.offlinemusicplayer.data.local.entity.PlaylistEntity.Companion.DEFAULT_PLAYLIST_MAP
import com.example.offlinemusicplayer.domain.enum_classes.PlaylistOptions
import com.example.offlinemusicplayer.domain.enum_classes.PlaylistSongOptions
import com.example.offlinemusicplayer.presentation.components.PlaylistOptionsDropDown
import com.example.offlinemusicplayer.presentation.components.PlaylistSongList
import com.example.offlinemusicplayer.presentation.dialogs.CreatePlaylistDialog
import com.example.offlinemusicplayer.presentation.dialogs.DeleteConfirmDialog
import com.example.offlinemusicplayer.presentation.dialogs.SongSelectionDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistDetailScreen(
    onBackPress: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: PlaylistDetailVM = hiltViewModel()
    val playlist by viewModel.playlist.collectAsStateWithLifecycle()
    val songs = viewModel.songs

    var menuExpanded by remember {
        mutableStateOf(false)
    }

    var showSongSelection by remember {
        mutableStateOf(false)
    }

    var showCreatePlaylistDialog by remember {
        mutableStateOf(false)
    }

    var playlistTitle by remember {
        mutableStateOf("")
    }

    var showDeleteDialog by remember {
        mutableStateOf(false)
    }

    val icon by remember(playlist) {
        when(playlist?.name) {
            PlaylistEntity.RECENTLY_PLAYED_PLAYLIST_NAME -> {
                mutableStateOf(Icons.Filled.History)
            }
            PlaylistEntity.MOST_PLAYED_PLAYLIST_NAME -> {
                mutableStateOf(Icons.AutoMirrored.Default.TrendingUp)
            }
            PlaylistEntity.FAVORITES_NAME -> {
                mutableStateOf(Icons.Filled.Favorite)
            }
            else -> {
                mutableStateOf(Icons.Filled.MusicNote)
            }
        }
    }

    if(showDeleteDialog) {
        DeleteConfirmDialog(
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                viewModel.deletePlaylist()
                showDeleteDialog = false
                onBackPress()
            },
            description = "\"${playlist?.name}\" will be permanently deleted."
        )
    }

    if(showSongSelection) {
        SongSelectionDialog(
            songs = viewModel.songs,
            title = playlistTitle,
            selectedSongIds = playlist?.songIds,
            onSubmit = { selectedSongs ->
                if(playlist != null) {
                    viewModel.updatePlaylistContent(selectedSongs)
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
            isCreatePlaylist = false,
            onCreate = {
                viewModel.updatePlaylistName(it)
                showCreatePlaylistDialog = false
            }
        )
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onBackPress,
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.onBackground
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "Back",
                )
            }

            Text(
                text = "Playlist",
                style = MaterialTheme.typography.titleLarge
            )

            Box {
                IconButton(
                    onClick = {
                        menuExpanded = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options"
                    )
                }
                PlaylistOptionsDropDown(
                    menuExpanded = menuExpanded,
                    onDismiss = {
                        menuExpanded = false
                    },
                    onOptionSelected = { option  ->
                        when(option) {
                            PlaylistOptions.Play -> {
                                viewModel.playAllSongsOfPlaylist()
                            }
                            PlaylistOptions.AddToQueue -> {
                                viewModel.addAllSongsToQueue(context = context)
                            }
                            PlaylistOptions.EditName -> {
                                playlistTitle = playlist?.name ?: ""
                                showCreatePlaylistDialog = true
                            }
                            PlaylistOptions.EditContent -> {
                                showSongSelection = true
                            }
                            PlaylistOptions.Delete -> {
                                showDeleteDialog = true
                            }
                        }
                    },
                    isDefaultPlaylist = playlist?.id in DEFAULT_PLAYLIST_MAP
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    imageVector = icon,
                    contentDescription = "${playlist?.name} playlist icon",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = playlist?.name ?: "",
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(
                    text = "${songs.size} Songs",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        if(songs.isNotEmpty()) {
            PlaylistSongList(
                songs = songs,
                onSongClick = { song, index ->
                    viewModel.playSong(index)
                },
                onSongMoved = { from, to ->
                    viewModel.moveSong(from, to)
                },
                onOptionSelected = { song, option ->
                    when(option) {
                        PlaylistSongOptions.PlayNext -> {
                            viewModel.playNext(song)
                        }
                        PlaylistSongOptions.AddToQueue -> {
                            viewModel.addToQueue(song)
                        }
                        PlaylistSongOptions.RemoveFromPlaylist -> {
                            viewModel.removeSongFromPlaylist(song)
                        }
                    }
                },
                isDefaultPlaylist = playlist?.id in DEFAULT_PLAYLIST_MAP,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
            )
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No songs added in playlist",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}