package com.example.offlinemusicplayer.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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

private const val SCROLL_MAG_OFFSET_DIVISOR = 4

@Composable
fun VerticalAlphabetScroller(
    onLetterSelect: (String) -> Unit,
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
                            scope.launch { onLetterSelect(letter) }
                        }
                    },
                    onVerticalDrag = { change, _ ->
                        dragPosition = change.position
                        val letter = getLetterForOffset(change.position.y)
                        if (selectedLetter != letter) {
                            selectedLetter = letter
                            scope.launch { onLetterSelect(letter) }
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
                sourceCenter = { Offset(componentWidth / SCROLL_MAG_OFFSET_DIVISOR, dragPosition.y) },
                magnifierCenter = {
                    // Position the magnifier to the left of the scroller
                    with(density) {
                        Offset(
                            x = (componentWidth / SCROLL_MAG_OFFSET_DIVISOR) - 40.dp.toPx(),
                            y = dragPosition.y - 80.dp.toPx()
                        )
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
