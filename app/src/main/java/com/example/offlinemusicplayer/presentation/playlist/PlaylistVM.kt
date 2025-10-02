package com.example.offlinemusicplayer.presentation.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.offlinemusicplayer.domain.usecase.GetPlaylists
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class PlaylistViewModel @Inject constructor(
    private val getPlaylists: GetPlaylists
) : ViewModel() {

    val playlists = getPlaylists().stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        emptyList()
    )
}
