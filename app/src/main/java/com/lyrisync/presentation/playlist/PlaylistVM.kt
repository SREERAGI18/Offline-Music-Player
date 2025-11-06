package com.lyrisync.presentation.playlist

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lyrisync.domain.model.Playlist
import com.lyrisync.domain.model.Song
import com.lyrisync.domain.usecase.playlist.PlaylistUseCases
import com.lyrisync.domain.usecase.songs.GetAllSongs
import com.lyrisync.domain.usecase.songs.GetSongsByIds
import com.lyrisync.player.PlayerServiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PlaylistVM @Inject constructor(
    getAllSongs: GetAllSongs,
    private val getSongsByIds: GetSongsByIds,
    private val playlistUseCases: PlaylistUseCases,
    private val playerRepository: PlayerServiceRepository
) : ViewModel() {

    var songs: List<Song> = emptyList()

    val currentMedia = playerRepository.currentMedia
    var playlistToModify: Playlist? = null

    init {
        viewModelScope.launch {

            getAllSongs().collectLatest {
                songs = it
            }
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
            playlistToModify?.let { playlist ->
                playlistUseCases.updatePlaylist(
                    playlist.copy(name = playlistName)
                )
                playlistToModify = null
            }
        }
    }

    fun updatePlaylistContent(songs: List<Song>) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistToModify?.let { playlist ->
                val songIds = songs.map { it.id }
                playlistUseCases.updatePlaylist(
                    playlist.copy(songIds = songIds)
                )
                playlistToModify = null
            }
        }
    }

    fun deletePlaylist() {
        viewModelScope.launch(Dispatchers.IO) {
            playlistToModify?.let { playlist ->
                playlistUseCases.deletePlaylist(playlist)
            }
        }
    }

    fun addAllSongsToQueue(context: Context, playlist: Playlist) {
        viewModelScope.launch(Dispatchers.IO) {
            val songs = getSongsByIds(playlist.songIds)
            playerRepository.addMedia(songs)
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "${songs.size} Songs added to queue", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun playAllSongsOfPlaylist(playlist: Playlist) {
        viewModelScope.launch(Dispatchers.IO) {
            val songs = getSongsByIds(playlist.songIds)
            playerRepository.setMediaList(songs)
            playerRepository.play()
        }
    }
}
