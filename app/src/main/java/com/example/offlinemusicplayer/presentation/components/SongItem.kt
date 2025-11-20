package com.example.offlinemusicplayer.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.offlinemusicplayer.domain.enum_classes.SongOptions
import com.example.offlinemusicplayer.domain.model.Song

@Composable
fun SongItem(
    modifier: Modifier = Modifier,
    song: Song,
    isPlaying: Boolean,
    onSongClick: () -> Unit,
    onOptionSelected: ((SongOptions) -> Unit)? = null
) {
    val context = LocalContext.current

    var menuExpanded by remember {
        mutableStateOf(false)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .clickable {
                onSongClick()
            }
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CachedAlbumArt(
            song = song,
            contentDescription = "Album art for ${song.title}",
            modifier = Modifier
                .size(56.dp)
                .clip(shape = RoundedCornerShape(8.dp),)
                .background(color = MaterialTheme.colorScheme.surfaceVariant)
                .padding(if(song.getExistingAlbumUri(context) == null) 10.dp else 0.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = song.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = if(isPlaying)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onBackground
                ),
                maxLines = 1
            )
            Text(
                text = song.artist ?: "",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        IconButton(
            onClick = {
                onOptionSelected?.invoke(SongOptions.UpdateFavorite)
            }
        ) {
            val icon = if(song.isFav) {
                Icons.Default.Favorite
            } else {
                Icons.Default.FavoriteBorder
            }
            Icon(
                imageVector = icon,
                contentDescription = "Favorite Icon",
            )
        }

        if(onOptionSelected != null) {
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
                SongOptionsDropDown(
                    menuExpanded = menuExpanded,
                    onDismiss = {
                        menuExpanded = false
                    },
                    onOptionSelected = onOptionSelected
                )
            }
        }
    }
}

@Composable
private fun SongOptionsDropDown(
    menuExpanded: Boolean,
    onDismiss: () -> Unit,
    onOptionSelected: ((SongOptions) -> Unit)? = null,
) {
    val options = SongOptions.entries.toList()

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
                    onOptionSelected?.invoke(option)
                    onDismiss()
                }
            )
        }
    }
}