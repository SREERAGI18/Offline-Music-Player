
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import com.example.offlinemusicplayer.domain.enum_classes.QueueSongOptions
import com.example.offlinemusicplayer.domain.model.Song
import com.example.offlinemusicplayer.presentation.components.QueueSongItem

@Composable
fun QueueSongsList(
    modifier: Modifier = Modifier,
    songs: List<Song>,
    onSongClick: (Song, Int) -> Unit,
    onSongMoved: (Int, Int) -> Unit,
    onOptionSelected: (Int, QueueSongOptions) -> Unit
) {
    var songList by remember { mutableStateOf(songs) }
    var draggedItemIndex by remember { mutableStateOf<Int?>(null) }
    var dragStartIndex = -1
    var dragEndIndex = -1
    var dragOffsetY by remember { mutableFloatStateOf(0f) }

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

            QueueSongItem(
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
                    val currentDraggedItemIndex = draggedItemIndex ?: return@QueueSongItem
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
                    onOptionSelected(index, option)
                },
                modifier = Modifier.graphicsLayer {
                    // Apply visual effect to the item being dragged
                    translationY = if (isBeingDragged) dragOffsetY else 0f
                    shadowElevation = if (isBeingDragged) 8f else 0f
                }
            )
        }
    }
}