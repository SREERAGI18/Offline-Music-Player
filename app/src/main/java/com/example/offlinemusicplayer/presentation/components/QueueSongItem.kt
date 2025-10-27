package com.example.offlinemusicplayer.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragHandle
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.offlinemusicplayer.domain.enum_classes.QueueSongOptions
import com.example.offlinemusicplayer.domain.model.Song

@Composable
fun QueueSongItem(
    modifier: Modifier = Modifier,
    song: Song,
    onSongClick: () -> Unit,
    onOptionSelected: (QueueSongOptions) -> Unit,
    onDragStart: () -> Unit,
    onDragEnd: () -> Unit,
    onDrag: (Float) -> Unit
) {
    var menuExpanded by remember {
        mutableStateOf(false)
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onSongClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { /* Clicks are disabled during drag, so this is safe */ },
            modifier = Modifier.pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        onDragStart()
                    },
                    onDragEnd = {
                        onDragEnd()
                    },
                    onDragCancel = {
                        onDragEnd() // Treat cancel as the end of the drag
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        onDrag(dragAmount.y) // We only care about vertical drag for reordering
                    }
                )
            }
        ) {
            Icon(
                imageVector = Icons.Default.DragHandle,
                contentDescription = "Drag to reorder"
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = song.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = if(song.isPlaying)
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
            QueueOptionsDropDown(
                menuExpanded = menuExpanded,
                onDismiss = {
                    menuExpanded = false
                },
                onOptionSelected = onOptionSelected
            )
        }
    }
}

@Composable
private fun QueueOptionsDropDown(
    menuExpanded: Boolean,
    onDismiss: () -> Unit,
    onOptionSelected: (QueueSongOptions) -> Unit,
) {
    val options = QueueSongOptions.entries.toList()

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
                    onDismiss
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QueueSongItemPreview() {
    QueueSongItem(
        song = Song.testSong(),
        onSongClick = {},
        onDragStart = {},
        onDragEnd = {},
        onDrag = {},
        onOptionSelected = {}
    )
}