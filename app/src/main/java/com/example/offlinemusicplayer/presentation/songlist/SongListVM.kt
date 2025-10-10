package com.example.offlinemusicplayer.presentation.songlist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.example.offlinemusicplayer.data.local.entity.SongsEntity
import com.example.offlinemusicplayer.domain.usecase.GetAllSongs
import com.example.offlinemusicplayer.domain.usecase.GetAllSongsPaginated
import com.example.offlinemusicplayer.player.PlayerServiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SongListVM @Inject constructor(
    getAllSongsPaginated: GetAllSongsPaginated,
    private val getAllSongs: GetAllSongs,
    private val playerRepository: PlayerServiceRepository,
) : ViewModel() {

    val songs: Flow<PagingData<SongsEntity>> = getAllSongsPaginated()

    fun setMediaList(initialSongPosition:Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val songs = getAllSongs()
            Log.e("SongListVM", "songs: $songs")
            withContext(Dispatchers.Main) {
                playerRepository.setMediaList(mediaList = songs, index = initialSongPosition)
            }
        }
    }

    fun playSong(index: Int) {
        if(playerRepository.getMediaList().isEmpty()) {
            setMediaList(index)
        }
        playerRepository.skipToMediaByIndex(index)
        playerRepository.play()
    }

    fun refresh() {
        viewModelScope.launch {
//            repository.forceRefresh()
        }
    }

    override fun onCleared() {
        super.onCleared()
//        repository.cleanup()
    }
}