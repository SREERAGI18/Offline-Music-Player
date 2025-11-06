package com.example.offlinemusicplayer.presentation.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.offlinemusicplayer.R
import com.example.offlinemusicplayer.domain.model.Song

@Composable
fun SongSelectionDialog(
    modifier: Modifier = Modifier,
    songs: List<Song>,
    selectedSongIds: List<Long>? = emptyList(),
    onSubmit: (List<Song>) -> Unit,
    onCancel: () -> Unit,
    title: String = stringResource(id = R.string.select_songs)
) {
    val songsList = remember { mutableStateListOf<Song>() }

    LaunchedEffect(songs) {
        songsList.clear()
        songsList.addAll(songs.map { it.copy() })
    }

    LaunchedEffect(Unit) {
        songsList.forEach { song ->
            song.selected = selectedSongIds?.contains(song.id) == true
        }
    }

    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
        ),
        content = {
            Card(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Select songs for $title",
                        style = MaterialTheme.typography.titleMedium.copy(
                            textAlign = TextAlign.Start,
                        ),
                        maxLines = 1,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(songsList) { song ->
                            SongSelectionItem(
                                song = song,
                                onSongClicked = {
                                    val index = songsList.indexOfFirst { it.id == song.id }
                                    if (index != -1) {
                                        songsList[index] = songsList[index].copy(selected = !songsList[index].selected)
                                    }
                                }
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {

                        TextButton(
                            onClick = onCancel,
                        ) {
                            Text(
                                text = stringResource(id = R.string.cancel),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        TextButton(
                            onClick = {
                                onSubmit(songsList.filter { it.selected })
                            },
                        ) {
                            Text(
                                text = stringResource(id = R.string.submit),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun SongSelectionItem(
    song: Song,
    onSongClicked: () -> Unit
) {
    val checkBoxImage = if (song.selected) {
        Icons.Default.CheckBox
    } else {
        Icons.Default.CheckBoxOutlineBlank
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onSongClicked()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onSongClicked
        ) {
            Icon(
                imageVector = checkBoxImage,
                contentDescription = "Checkbox image"
            )
        }
        Column {
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