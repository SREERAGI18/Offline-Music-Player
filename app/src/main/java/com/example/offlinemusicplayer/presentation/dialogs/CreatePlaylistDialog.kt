package com.example.offlinemusicplayer.presentation.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.offlinemusicplayer.R

@Composable
fun CreatePlaylistDialog(
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit,
    initialName: String,
    isCreatePlaylist: Boolean,
) {
    var playlistName by remember {
        mutableStateOf(initialName)
    }

    val title =
        if (isCreatePlaylist) {
            R.string.create_playlist
        } else {
            R.string.edit_playlist
        }

    val confirmButtonText =
        if (isCreatePlaylist) {
            R.string.create
        } else {
            R.string.save
        }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(16.dp),
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    text = stringResource(title),
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(modifier = Modifier.height(20.dp))
                TextField(
                    value = playlistName,
                    onValueChange = {
                        playlistName = it
                    },
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onCreate(playlistName)
                },
            ) {
                Text(
                    text = stringResource(id = confirmButtonText),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
            ) {
                Text(
                    text = stringResource(id = R.string.cancel),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        },
    )
}
