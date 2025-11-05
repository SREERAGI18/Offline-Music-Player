package com.example.offlinemusicplayer.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.example.offlinemusicplayer.util.scrollMagnifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun VerticalAlphabetScroller(
    onLetterSelected: (String) -> Unit,
    scope: CoroutineScope,
    modifier: Modifier = Modifier
) {
    val alphabet = remember { listOf("#") + ('A'..'Z').map { it.toString() } }
    var componentHeight by remember { mutableFloatStateOf(0f) }
    var componentWidth by remember { mutableFloatStateOf(0f) }
    var selectedLetter by remember { mutableStateOf<String?>(null) }

    var isDragging by remember { mutableStateOf(false) }
    var dragPosition by remember { mutableStateOf(Offset.Zero) }
    val density = LocalDensity.current

    val letterHeight = remember(componentHeight) {
        if (componentHeight > 0) componentHeight / alphabet.size else 0f
    }

    fun getLetterForOffset(offsetY: Float): String {
        if (letterHeight == 0f) return alphabet.first()
        val index = (offsetY / letterHeight).toInt().coerceIn(0, alphabet.lastIndex)
        return alphabet[index]
    }

    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(Color.Transparent)
            .padding(horizontal = 4.dp)
            .onGloballyPositioned {
                componentHeight = it.size.height.toFloat()
                componentWidth = it.size.width.toFloat()
            }
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragStart = { startOffset ->
                        isDragging = true
                        dragPosition = startOffset
                        val letter = getLetterForOffset(startOffset.y)
                        if (selectedLetter != letter) {
                            selectedLetter = letter
                            scope.launch { onLetterSelected(letter) }
                        }
                    },
                    onVerticalDrag = { change, _ ->
                        dragPosition = change.position
                        val letter = getLetterForOffset(change.position.y)
                        if (selectedLetter != letter) {
                            selectedLetter = letter
                            scope.launch { onLetterSelected(letter) }
                        }
                    },
                    onDragEnd = {
                        isDragging = false
                        selectedLetter = null
                    },
                    onDragCancel = {
                        isDragging = false
                        selectedLetter = null
                    }
                )
            }
            .scrollMagnifier(
                sourceCenter = { Offset(componentWidth / 4, dragPosition.y) },
                magnifierCenter = {
                    // Position the magnifier to the left of the scroller
                    with(density) {
                        Offset((componentWidth / 4) - 40.dp.toPx(), dragPosition.y)
                    }
                },
                size = DpSize(50.dp, 50.dp),
                cornerRadius = 50f,
                visible = isDragging
            ),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        alphabet.forEach { letter ->
            Text(
                text = letter,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.weight(1f)
            )
        }
    }
}