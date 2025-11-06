package com.lyrisync.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.lyrisync.data.local.entity.PlaylistEntity
import com.lyrisync.data.local.entity.PlaylistEntity.Companion.DEFAULT_PLAYLIST_IDS
import com.lyrisync.domain.enum_classes.OptionType
import com.lyrisync.domain.enum_classes.PlaylistOptions
import com.lyrisync.domain.model.Playlist

@Composable
fun PlaylistItem(
    playlist: Playlist,
    modifier: Modifier = Modifier,
    onClick: (Playlist) -> Unit,
    onOptionSelected: (PlaylistOptions) -> Unit,
) {
    var menuExpanded by remember {
        mutableStateOf(false)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(playlist) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            // Determine which icon to show for default playlists
            when (playlist.name) {
                PlaylistEntity.RECENTLY_PLAYED_PLAYLIST_NAME -> {
                    Image(
                        imageVector = Icons.Filled.History,
                        contentDescription = "${playlist.name} playlist icon",
                        modifier = Modifier.fillMaxSize(),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
                else -> { // Default for user-created playlists
                    Image(
                        imageVector = Icons.Filled.MusicNote,
                        contentDescription = "${playlist.name} playlist icon",
                        modifier = Modifier.fillMaxSize(),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f),

        ) {
            Text(
                text = playlist.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${playlist.songIds.size} Songs",
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

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
                onOptionSelected = onOptionSelected,
                isDefaultPlaylist = playlist.id in DEFAULT_PLAYLIST_IDS
            )
        }
    }
}

@Composable
fun PlaylistOptionsDropDown(
    menuExpanded: Boolean,
    onDismiss: () -> Unit,
    onOptionSelected: (PlaylistOptions) -> Unit,
    isDefaultPlaylist: Boolean
) {
    val options = PlaylistOptions
        .entries
        .toList()
        .filter {
            if(isDefaultPlaylist) {
                it.type != OptionType.Modify
            } else {
                true
            }
        }

    DropdownMenu(
        expanded = menuExpanded,
        onDismissRequest = onDismiss
    ) {
        for (option in options) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = option.displayName,
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                onClick = {
                    onOptionSelected(option)
                    onDismiss()
                }
            )
        }
    }
}