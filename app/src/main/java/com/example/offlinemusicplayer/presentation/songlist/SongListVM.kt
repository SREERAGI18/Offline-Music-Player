package com.example.offlinemusicplayer.presentation.songlist

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.example.offlinemusicplayer.domain.model.Song
import com.example.offlinemusicplayer.domain.usecase.GetAllSongs
import com.example.offlinemusicplayer.domain.usecase.GetAllSongsPaginated
import com.example.offlinemusicplayer.player.PlayerServiceRepository
import com.example.offlinemusicplayer.util.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SongListVM @Inject constructor(
    getAllSongsPaginated: GetAllSongsPaginated,
    private val getAllSongs: GetAllSongs,
    private val playerRepository: PlayerServiceRepository,
) : ViewModel() {

    val songs = mutableStateListOf<Song>()
    val currentMediaIndex = playerRepository.currentMediaIndex

    init {
        setMediaList(0)
        viewModelScope.launch {
            songs.addAll(getAllSongs())
        }
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
}