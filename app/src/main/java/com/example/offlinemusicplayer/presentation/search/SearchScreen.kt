package com.example.offlinemusicplayer.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.offlinemusicplayer.domain.enum_classes.SongOptions
import com.example.offlinemusicplayer.presentation.components.SongsList

@Composable
fun SearchScreen() {
    val viewModel: SearchVM = hiltViewModel()
    val songs = viewModel.songs.collectAsLazyPagingItems()

    val focusRequester = remember { FocusRequester() }

    var query by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(query) {
        viewModel.updateSearchQuery(query)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                )
                .clip(RoundedCornerShape(8.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            if (query.isEmpty()) {
                Text(
                    text = "Search songs...",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            BasicTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { /* a soft keyboard might be hidden here */ }),
                textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        SongsList(
            onSongClick = { song, index ->
                viewModel.playSong(index)
            },
            onOptionSelected = { song, option ->
                when(option) {
                    SongOptions.PlayNext -> {
                        viewModel.playNext(song)
                    }
                    SongOptions.AddToQueue -> {
                        viewModel.addToQueue(song)
                    }
                    SongOptions.EditSongInfo -> {

                    }
                    SongOptions.Delete -> {

                    }
                    SongOptions.Details -> {

                    }
                }
            },
            songs = songs,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}