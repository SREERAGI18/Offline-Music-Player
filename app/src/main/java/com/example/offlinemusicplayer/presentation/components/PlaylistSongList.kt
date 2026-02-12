package com.example.offlinemusicplayer.presentation.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import com.example.offlinemusicplayer.domain.enum_classes.PlaylistSongOptions
import com.example.offlinemusicplayer.domain.model.Song
import com.example.offlinemusicplayer.ui.theme.shadow

@Composable
fun PlaylistSongList(
    modifier: Modifier = Modifier,
    songs: List<Song>,
    onSongClick: (Song, Int) -> Unit,
    onSongMoved: (Int, Int) -> Unit,
    onOptionSelected: (Song, PlaylistSongOptions) -> Unit,
    isDefaultPlaylist: Boolean
) {
    var songList by remember { mutableStateOf(songs) }
    var draggedItemIndex by remember { mutableStateOf<Int?>(null) }
    var dragStartIndex = -1
    var dragEndIndex = -1
    var dragOffsetY by remember { mutableFloatStateOf(0f) }

    val shadowColor = MaterialTheme.colorScheme.shadow

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
    ) {
        itemsIndexed(
            items = songList,
            key = { _, song -> song.id }
        ) { index, song ->
            // Find the current position of the item being dragged
            val currentItemIndex = draggedItemIndex?.let { songList.indexOf(songList[it]) }

            val isBeingDragged = index == currentItemIndex

            PlaylistSongItem(
                song = song,
                onSongClick = {
                    onSongClick(song, index)
                },
                onDragStart = {
                    draggedItemIndex = index
                    dragStartIndex = index
                },
                onDragEnd = {
                    draggedItemIndex = null
                    dragOffsetY = 0f

                    onSongMoved(dragStartIndex, dragEndIndex)
                },
                onDrag = { dragAmount ->
                    dragOffsetY += dragAmount

                    // Logic to swap items as you drag over them
                    val currentDraggedItemIndex = draggedItemIndex ?: return@PlaylistSongItem
                    val draggedItem = songList[currentDraggedItemIndex]

                    // Determine where to move the item
                    // This is a simplified example. A robust implementation would use item heights.
                    val newIndex = (currentDraggedItemIndex + (dragOffsetY / 150f).toInt())
                        .coerceIn(0, songList.size - 1)

                    if (newIndex != currentDraggedItemIndex) {
                        songList = songList.toMutableList().apply {
                            removeAt(currentDraggedItemIndex)
                            add(newIndex, draggedItem)
                        }
                        draggedItemIndex = newIndex
                        dragEndIndex = newIndex
                        dragOffsetY = 0f // Reset offset after swap
                    }
                },
                onOptionSelected = { option ->
                    onOptionSelected(song, option)
                },
                isDefaultPlaylist = isDefaultPlaylist,
                modifier = Modifier.graphicsLayer {
                    // Apply visual effect to the item being dragged
                    translationY = if (isBeingDragged) dragOffsetY else 0f
                    shadowElevation = if (isBeingDragged) 6f else 0f
                    ambientShadowColor = shadowColor
                    spotShadowColor = shadowColor
                }
            )
        }
    }
}