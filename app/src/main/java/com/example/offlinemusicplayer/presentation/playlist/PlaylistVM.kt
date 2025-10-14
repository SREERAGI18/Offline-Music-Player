package com.example.offlinemusicplayer.presentation.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.offlinemusicplayer.domain.model.Song
import com.example.offlinemusicplayer.domain.usecase.CreatePlaylist
import com.example.offlinemusicplayer.domain.usecase.GetAllSongs
import com.example.offlinemusicplayer.domain.usecase.GetPlaylists
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistVM @Inject constructor(
    getPlaylists: GetPlaylists,
    getAllSongs: GetAllSongs,
    private val createPlaylist: CreatePlaylist
) : ViewModel() {

    var songs: List<Song> = emptyList()

    init {
        viewModelScope.launch {
            songs = getAllSongs()
        }
    }

    val playlists = getPlaylists().stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        emptyList()
    )

    fun addPlaylist(playlistName: String, songs: List<Song>) {
        viewModelScope.launch(Dispatchers.IO) {
            val songIds = songs.map { it.id }
            createPlaylist(playlistName, songIds)
        }
    }
}
