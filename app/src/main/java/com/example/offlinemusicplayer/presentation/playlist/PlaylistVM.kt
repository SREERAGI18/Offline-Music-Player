package com.example.offlinemusicplayer.presentation.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.offlinemusicplayer.domain.model.Playlist
import com.example.offlinemusicplayer.domain.model.Song
import com.example.offlinemusicplayer.domain.usecase.playlist.PlaylistUseCases
import com.example.offlinemusicplayer.domain.usecase.songs.GetAllSongs
import com.example.offlinemusicplayer.player.PlayerServiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistVM @Inject constructor(
    getAllSongs: GetAllSongs,
    private val playlistUseCases: PlaylistUseCases,
    playerRepository: PlayerServiceRepository
) : ViewModel() {

    var songs: List<Song> = emptyList()

    val currentMedia = playerRepository.currentMedia
    var playlistToUpdate: Playlist? = null

    init {
        viewModelScope.launch {
            songs = getAllSongs()
        }
    }

    val playlists = playlistUseCases.getPlaylists().stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        emptyList()
    )

    fun addPlaylist(playlistName: String, songs: List<Song>) {
        viewModelScope.launch(Dispatchers.IO) {
            val songIds = songs.map { it.id }
            playlistUseCases.createPlaylist(playlistName, songIds)
        }
    }

    fun updatePlaylistName(playlistName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistToUpdate?.let { playlist ->
                playlistUseCases.updatePlaylist(
                    playlist.copy(name = playlistName)
                )
                playlistToUpdate = null
            }
        }
    }

    fun updatePlaylistContent(songs: List<Song>) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistToUpdate?.let { playlist ->
                val songIds = songs.map { it.id }
                playlistUseCases.updatePlaylist(
                    playlist.copy(songIds = songIds)
                )
                playlistToUpdate = null
            }
        }
    }
}
