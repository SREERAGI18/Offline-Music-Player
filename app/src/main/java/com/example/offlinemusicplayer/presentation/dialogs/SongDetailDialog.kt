package com.example.offlinemusicplayer.presentation.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.offlinemusicplayer.domain.model.Song
import com.example.offlinemusicplayer.player.MediaInfoExtractor
import com.example.offlinemusicplayer.util.toFileSize
import com.example.offlinemusicplayer.util.toFormattedDate
import com.example.offlinemusicplayer.util.toFormattedTime

@Composable
fun SongDetailDialog(
    song: Song,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    text = "OK",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        },
        title = {
            SongDetailContent(song = song)
        },
        confirmButton = {},
    )
}

@Composable
private fun SongDetailContent(song: Song) {
    val mediaInfo = MediaInfoExtractor.extractAudioInfo(song.path)
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Details",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        SongDetailItem(
            title = "Name",
            value = song.title
        )
        Spacer(modifier = Modifier.height(8.dp))
        SongDetailItem(
            title = "Path",
            value = song.path
        )
        Spacer(modifier = Modifier.height(8.dp))
        SongDetailItem(
            title = "Size",
            value = song.size.toFileSize()
        )
        Spacer(modifier = Modifier.height(8.dp))
        SongDetailItem(
            title = "Format",
            value = mediaInfo?.format ?: ""
        )
        Spacer(modifier = Modifier.height(8.dp))
        SongDetailItem(
            title = "Last Modified",
            value = song.dateModified.toFormattedDate()
        )
        Spacer(modifier = Modifier.height(8.dp))
        SongDetailItem(
            title = "Duration",
            value = song.duration.toFormattedTime()
        )
        Spacer(modifier = Modifier.height(8.dp))
        SongDetailItem(
            title = "Bitrate",
            value = "${mediaInfo?.bitRateInKbps} kbps"
        )
        Spacer(modifier = Modifier.height(8.dp))
        SongDetailItem(
            title = "Sampling rate",
            value = "${mediaInfo?.samplingRate} Hz"
        )
    }
}

@Composable
private fun SongDetailItem(
    title: String,
    value: String
) {
    val annotatedString = buildAnnotatedString {
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append("$title: ")
        }
        append(value)
    }

    Text(
        text = annotatedString,
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Start
    )
}
