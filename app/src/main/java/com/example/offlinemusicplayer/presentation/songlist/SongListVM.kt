package com.example.offlinemusicplayer.presentation.songlist

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.example.offlinemusicplayer.domain.model.Playlist
import com.example.offlinemusicplayer.domain.model.Song
import com.example.offlinemusicplayer.domain.usecase.DeleteSongById
import com.example.offlinemusicplayer.domain.usecase.GetAllSongs
import com.example.offlinemusicplayer.domain.usecase.GetAllSongsPaginated
import com.example.offlinemusicplayer.domain.usecase.GetPlaylists
import com.example.offlinemusicplayer.domain.usecase.UpdatePlaylist
import com.example.offlinemusicplayer.player.PlayerServiceRepository
import com.example.offlinemusicplayer.util.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SongListVM @Inject constructor(
    getAllSongsPaginated: GetAllSongsPaginated,
    private val getAllSongs: GetAllSongs,
    private val getPlaylists: GetPlaylists,
    private val updatePlaylist: UpdatePlaylist,
    private val deleteSongById: DeleteSongById,
    private val playerRepository: PlayerServiceRepository,
) : ViewModel() {

    val songs = mutableStateListOf<Song>()
    val currentMedia = playerRepository.currentMedia

    val playlists = mutableStateListOf<Playlist>()

    private val _deleteProgress: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val deleteProgress = _deleteProgress.asStateFlow()

    init {
        setMediaList(0)
        viewModelScope.launch {
            songs.addAll(getAllSongs())
        }
        getPlaylist()
    }

    fun setMediaList(initialSongPosition:Int) {
        viewModelScope.launch(Dispatchers.IO) {
            playerRepository.connected.collectLatest {
                if(!it) return@collectLatest

                val songs = getAllSongs()
                Logger.logError("SongListVM", "songs: $songs")
                withContext(Dispatchers.Main) {
                    playerRepository.setMediaList(mediaList = songs, index = initialSongPosition)
                }
            }
        }
    }

    fun playSong(index: Int) {
        setMediaList(index)
        playerRepository.skipToMediaByIndex(index)
        playerRepository.play()
    }

    fun getMediaIndex(currentMedia: Song?): Int {
        val index = songs.indexOfFirst { it.id == currentMedia?.id }

        return index
    }

    fun playNext(song: Song) {
        viewModelScope.launch {
            // Find if the song already exists in the playlist
            val existingIndex = playerRepository.findIndexOfSongInPlaylist(song.id)
            val currentIndex = playerRepository.getCurrentMediaIndex()
            val nextIndex = currentIndex + 1

            if (existingIndex != null) {
                // Song exists, move it
                playerRepository.moveMedia(existingIndex, nextIndex)
            } else {
                // Song doesn't exist, add it
                playerRepository.addMedia(nextIndex, song)
            }
        }
    }

    fun addToQueue(song: Song) {
        viewModelScope.launch {
            val existingIndex = playerRepository.findIndexOfSongInPlaylist(song.id)
            if (existingIndex == null) {
                // Only add the song if it's not already in the queue
                playerRepository.addMedia(song)
            }
        }
    }

    fun deleteSongFile(song: Song) {
        viewModelScope.launch {
            _deleteProgress.value = true
            deleteSongById(song)
            _deleteProgress.value = false
        }
    }

    fun getPlaylist() {
        viewModelScope.launch {
            getPlaylists().collectLatest {
                playlists.clear()
                playlists.addAll(it)
                Log.d("SongListVM", "playlists: $playlists")
            }
        }
    }

    fun addToPlaylist(song: Song, playlist: Playlist) {
        viewModelScope.launch {
            val updatedSongIds = playlist.songIds + song.id
            updatePlaylist(songIds = updatedSongIds, playlist = playlist)
        }
    }

}