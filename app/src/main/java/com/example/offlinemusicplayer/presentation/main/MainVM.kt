package com.example.offlinemusicplayer.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.offlinemusicplayer.domain.usecase.songs.SyncSongsWithDevice
import com.example.offlinemusicplayer.player.PlayerServiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainVM @Inject constructor(
    private val playerRepository: PlayerServiceRepository,
    private val syncSongsWithDevice: SyncSongsWithDevice,
): ViewModel() {

    private val _newlyAddedSongCount = MutableStateFlow(0)
    val newlyAddedSongCount = _newlyAddedSongCount.asStateFlow()

    val currentMedia = playerRepository.currentMedia

    val playerState = playerRepository.currentState

    val currentMediaPosition = playerRepository.mediaPosition

    val shuffleModeEnabled = playerRepository.shuffleModeEnabled

    val repeatMode = playerRepository.repeatMode

    init {
        viewModelScope.launch {
            _newlyAddedSongCount.value = syncSongsWithDevice()
        }
    }

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