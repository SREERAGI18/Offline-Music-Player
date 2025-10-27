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

    val currentMedia = playerRepository.currentMedia
    val currentQueue = mutableStateListOf<Song>()

    fun getCurrentMediaList() {
        val songs = playerRepository.getMediaList()

        Log.e("NowPlayingQueueVM", "getCurrentMediaList: $songs")

        currentQueue.addAll(songs)
    }

    fun updateCurrentPlaying(playingSongId: Long?) {
        // Find the index of the previously playing song
        val previouslyPlayingIndex = currentQueue.indexOfFirst { it.isPlaying }
        if (previouslyPlayingIndex != -1) {
            currentQueue[previouslyPlayingIndex] = currentQueue[previouslyPlayingIndex].copy(isPlaying = false)
        }

        // Find the index of the new playing song
        val nowPlayingIndex = currentQueue.indexOfFirst { it.id == playingSongId }
        if (nowPlayingIndex != -1) {
            currentQueue[nowPlayingIndex] = currentQueue[nowPlayingIndex].copy(isPlaying = true)
        }
    }

    fun removeSong(index: Int) {
        playerRepository.removeMedia(index)
    }

    fun moveSong(from: Int, to: Int) {
        currentQueue.add(to, currentQueue.removeAt(from))
        playerRepository.moveMedia(from, to)
    }

    fun playSong(index: Int) {
        playerRepository.skipToMediaByIndex(index)
        playerRepository.play()
    }
}