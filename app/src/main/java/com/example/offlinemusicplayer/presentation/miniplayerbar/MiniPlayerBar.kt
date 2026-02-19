package com.example.offlinemusicplayer.presentation.miniplayerbar

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.offlinemusicplayer.domain.enumclasses.PlayerState
import com.example.offlinemusicplayer.presentation.components.CachedAlbumArt
import com.example.offlinemusicplayer.presentation.main.MainVM
import com.example.offlinemusicplayer.presentation.nowplayingdetail.PlayerIconButton

@Composable
fun MiniPlayerBar(
    onClick: () -> Unit,
    viewModel: MainVM,
) {
    val currentSong by viewModel.currentMedia.collectAsStateWithLifecycle()
    val playerState by viewModel.playerState.collectAsStateWithLifecycle()

    val isPlaying = playerState == PlayerState.Playing
    var hasNext by remember { mutableStateOf(true) }
    var hasPrev by remember { mutableStateOf(true) }

    LaunchedEffect(currentSong, playerState) {
        hasNext = viewModel.hasNext()
        hasPrev = viewModel.hasPrevious()
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondary)
                .clickable { onClick() }
                .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        CachedAlbumArt(
            song = currentSong,
            contentDescription = "Album art for ${currentSong?.title}",
            modifier =
                Modifier
                    .size(40.dp)
                    .clip(
                        shape = RoundedCornerShape(8.dp),
                    ),
            contentScale = ContentScale.Crop,
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = currentSong?.title ?: "",
                style =
                    MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.onSecondary,
                    ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.basicMarquee(),
            )
            Text(
                text = currentSong?.artist ?: "",
                style =
                    MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSecondary,
                    ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        PlayerIconButton(
            onClick = { viewModel.skipToPrev() },
            icon = Icons.Filled.SkipPrevious,
            contentDescription = "Skip to previous",
            modifier = Modifier.size(30.dp),
            enabled = hasPrev,
        )

        Spacer(modifier = Modifier.width(8.dp))

        PlayerIconButton(
            onClick = { if (isPlaying) viewModel.pause() else viewModel.play() },
            icon = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
            contentDescription = if (isPlaying) "Pause" else "Play",
            modifier = Modifier.size(30.dp),
        )

        Spacer(modifier = Modifier.width(8.dp))

        PlayerIconButton(
            onClick = { viewModel.skipToNext() },
            icon = Icons.Filled.SkipNext,
            contentDescription = "Skip to next",
            modifier = Modifier.size(30.dp),
            enabled = hasNext,
        )
    }
}
