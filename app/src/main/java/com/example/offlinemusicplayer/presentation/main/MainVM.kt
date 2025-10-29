package com.example.offlinemusicplayer.presentation.main

import androidx.lifecycle.ViewModel
import com.example.offlinemusicplayer.player.PlayerServiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainVM @Inject constructor(
    private val playerRepository: PlayerServiceRepository
): ViewModel(){

    val currentMedia = playerRepository.currentMedia

    val playerState = playerRepository.currentState

    val currentMediaPosition = playerRepository.mediaPosition

    val shuffleModeEnabled = playerRepository.shuffleModeEnabled

    val repeatMode = playerRepository.repeatMode

    fun play() {
        playerRepository.play()
    }

    fun pause() {
        playerRepository.pause()
    }

    fun skipToPrev() {
        playerRepository.skipToPreviousMedia()
    }

    fun hasPrevious() = playerRepository.hasPreviousMedia()

    fun skipToNext() {
        playerRepository.skipToNextMedia()
    }

    fun hasNext() = playerRepository.hasNextMedia()

    fun seekTo(position: Long) {
        playerRepository.seekToPosition(position)
    }

    fun fastForwardBy10Secs() {
        playerRepository.seekForward()
    }

    fun rewindBy10Secs() {
        playerRepository.seekBack()
    }

    fun toggleShuffleMode() {
        playerRepository.setShuffleModeEnabled(!shuffleModeEnabled.value)
    }

    fun toggleRepeatMode() {
        val nextRepeatMode = repeatMode.value.nextRepeatMode()
        playerRepository.setRepeatMode(nextRepeatMode)
    }
}