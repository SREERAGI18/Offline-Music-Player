package com.example.offlinemusicplayer.presentation.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.offlinemusicplayer.domain.model.Song
import com.example.offlinemusicplayer.domain.usecase.playlist.CreatePlaylist
import com.example.offlinemusicplayer.domain.usecase.songs.GetAllSongs
import com.example.offlinemusicplayer.domain.usecase.playlist.GetPlaylists
import com.example.offlinemusicplayer.player.PlayerServiceRepository
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
    private val createPlaylist: CreatePlaylist,
    playerRepository: PlayerServiceRepository
) : ViewModel() {

    var songs: List<Song> = emptyList()

    val currentMedia = playerRepository.currentMedia

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
