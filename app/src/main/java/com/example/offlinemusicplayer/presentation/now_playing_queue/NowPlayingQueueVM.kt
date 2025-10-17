package com.example.offlinemusicplayer.presentation.now_playing_queue

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.offlinemusicplayer.domain.model.Song
import com.example.offlinemusicplayer.player.PlayerServiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NowPlayingQueueVM @Inject constructor(
    private val playerRepository: PlayerServiceRepository
): ViewModel() {

    fun getCurrentMediaList(): List<Song> {
        val songs = playerRepository.getMediaList()

        Log.e("NowPlayingQueueVM", "getCurrentMediaList: $songs")

        return songs
    }

    fun removeSong(index: Int) {
        playerRepository.removeMedia(index)
    }

    fun playSong(index: Int) {
        playerRepository.skipToMediaByIndex(index)
        playerRepository.play()
    }
}