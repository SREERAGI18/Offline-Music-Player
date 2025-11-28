package com.example.offlinemusicplayer.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun LyricsView(
    modifier: Modifier = Modifier,
    lyrics: Map<Long, String>,
    currentPosition: Long?
) {
    // If there are no lyrics, show a placeholder message.
    if (lyrics.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize().padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No lyrics available. Tap the lyrics icon to add an .lrc file.",
                style = MaterialTheme.typography.bodyLarge.copy(color = Color.White.copy(alpha = 0.7f)),
                textAlign = TextAlign.Center
            )
        }
        return
    }

    val lazyListState = rememberLazyListState()

    // `derivedStateOf` is used to efficiently calculate the current line index
    // only when `currentPosition` or `lyrics` change.
    val currentLineIndex by remember(lyrics) {
        derivedStateOf {
            // Find the last lyric line whose timestamp is less than or equal to the current position.
            lyrics.keys.indexOfLast { it <= (currentPosition ?: 0) }.coerceAtLeast(0)
        }
    }

    // This effect will automatically scroll the list to the current line.
    LaunchedEffect(currentLineIndex) {
        // `animateScrollToItem` provides a smooth scrolling animation.
        // We center the item by subtracting half the visible items from the index.
        val scrollPosition = (currentLineIndex - (lazyListState.layoutInfo.visibleItemsInfo.size / 2)).coerceAtLeast(0)
        if (lazyListState.firstVisibleItemIndex != scrollPosition) {
            lazyListState.animateScrollToItem(scrollPosition)
        }
    }

    val lyricsEntries = remember(lyrics) { lyrics.entries.toList() }

    LazyColumn(
        state = lazyListState,
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        itemsIndexed(lyricsEntries) { index, entry ->
            val lyricText = entry.value
            // Determine the color: highlighted if it's the current line, faded otherwise.
            val color = if (index == currentLineIndex) {
                Color.White
            } else {
                Color.White.copy(alpha = 0.5f)
            }

            Text(
                text = lyricText,
                style = MaterialTheme.typography.titleLarge,
                color = color,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}