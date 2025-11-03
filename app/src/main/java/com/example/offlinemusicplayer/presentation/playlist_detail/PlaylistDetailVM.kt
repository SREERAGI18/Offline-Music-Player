package com.example.offlinemusicplayer.presentation.playlist_detail

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.offlinemusicplayer.domain.model.Playlist
import com.example.offlinemusicplayer.domain.model.Song
import com.example.offlinemusicplayer.domain.usecase.GetPlaylistById
import com.example.offlinemusicplayer.domain.usecase.GetSongsByIds
import com.example.offlinemusicplayer.domain.usecase.GetSongsByIdsPaginated
import com.example.offlinemusicplayer.domain.usecase.RemoveSongFromPlaylist
import com.example.offlinemusicplayer.player.PlayerServiceRepository
import com.example.offlinemusicplayer.presentation.navigation.Screens
import com.example.offlinemusicplayer.util.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PlaylistDetailVM @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getPlaylistById: GetPlaylistById,
    private val getSongsByIdsPaginated: GetSongsByIdsPaginated,
    private val getSongsByIds: GetSongsByIds,
    private val removeSongFromPlaylist: RemoveSongFromPlaylist,
    private val playerRepository: PlayerServiceRepository,
): ViewModel() {
    private val playlistId: Long = savedStateHandle[Screens.PLAYLIST_ID_KEY] ?: 0L

    val playlist: StateFlow<Playlist?> = getPlaylistById(playlistId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val songs = mutableStateListOf<Song>()

    init {
        getSongs()
    }

    fun getSongs() {
        viewModelScope.launch {
            playlist.value?.let { currentPlayList ->
                songs.addAll(getSongsByIds(currentPlayList.songIds))
            }
        }
    }

    fun setMediaList(initialSongPosition:Int) {
        viewModelScope.launch(Dispatchers.IO) {
            Logger.logError("PlaylistDetailVM", "songs: $songs")
            withContext(Dispatchers.Main) {
                playerRepository.setMediaList(mediaList = songs, index = initialSongPosition)
            }
        }
    }

    fun playSong(index: Int) {
        setMediaList(index)
        playerRepository.skipToMediaByIndex(index)
        playerRepository.play()
    }

    fun removeSongFromPlaylist(song: Song) {
        viewModelScope.launch {
            removeSongFromPlaylist(playlistId, song.id)
            songs.remove(song)
        }
    }

    fun moveSong(from: Int, to: Int) {
        songs.add(to, songs.removeAt(from))
        playerRepository.moveMedia(from, to)
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
}