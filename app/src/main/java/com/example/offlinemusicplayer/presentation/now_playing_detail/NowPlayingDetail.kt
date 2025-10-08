package com.example.offlinemusicplayer.presentation.now_playing_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.offlinemusicplayer.presentation.main.MainVM

@Composable
fun NowPlayingDetail(
    viewModel: MainVM,
    onCollapse: () -> Unit
) {

    val currentSong by viewModel.currentMedia.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(currentSong?.title ?: "", style = MaterialTheme.typography.titleLarge)
        Text(currentSong?.artist ?: "", style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(MaterialTheme.colorScheme.primary)
        )

        Spacer(modifier = Modifier.height(24.dp))
        Text("Now Playing...", style = MaterialTheme.typography.bodySmall)

        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onCollapse) {
            Text("Collapse")
        }
    }
}
