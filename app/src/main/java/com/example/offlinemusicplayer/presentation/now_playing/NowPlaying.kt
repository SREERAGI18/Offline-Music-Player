package com.example.offlinemusicplayer.presentation.now_playing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.example.offlinemusicplayer.player.playbackStateFlow

@Composable
fun NowPlayingBar(controller: MediaController?) {
    if (controller == null) return

    val playbackState by controller.playbackStateFlow().collectAsState(initial = Player.STATE_IDLE)
    val title = controller.mediaMetadata.title ?: "No song"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title.toString())
        if (playbackState == Player.STATE_READY && controller.playWhenReady) {
            Text("Playing")
        } else {
            Text("Paused")
        }
    }
}