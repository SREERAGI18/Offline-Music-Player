package com.example.offlinemusicplayer.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.offlinemusicplayer.data.local.entity.SongsEntity
import com.example.offlinemusicplayer.domain.model.Song

@Composable
fun SongItem(
    modifier: Modifier = Modifier,
    song: Song,
    onSongClick: (Song) -> Unit,
) {
    val context = LocalContext.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .clickable {
                onSongClick(song)
            }
            .padding(16.dp),
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
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1
            )
            Text(
                text = song.artist ?: "",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1
            )
        }
    }
}