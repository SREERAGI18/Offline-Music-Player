package com.example.offlinemusicplayer.presentation.now_playing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import com.example.offlinemusicplayer.domain.enum_classes.PlayerState
import com.example.offlinemusicplayer.domain.model.Song
import com.example.offlinemusicplayer.player.PlayerServiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NowPlayingVM @Inject constructor(
    private val playerRepository: PlayerServiceRepository,
): ViewModel() {

    val currentPlayerState: StateFlow<PlayerState> = playerRepository.currentState
    val currentMedia: StateFlow<Song?> = playerRepository.currentMedia
    val mediaPosition: StateFlow<Long?> = playerRepository.mediaPosition
    val player: StateFlow<Player?> = playerRepository.player

    fun getSeekPosition():Long {
        return mediaPosition.value ?: 0L
    }

    fun play() {
        viewModelScope.launch {
            delay(300)
            playerRepository.play()
        }
    }

    fun playNext() {
        playerRepository.skipToNextMedia()
    }

    fun playPrev() {
        playerRepository.skipToPreviousMedia()
    }

    fun pause() {
        playerRepository.pause()
    }

    fun stop() {
        playerRepository.stop()
    }

    fun rewind() {
        playerRepository.seekBack()
    }

    fun fastForward() {
        playerRepository.seekForward()
    }

    fun seekToPosition(seekPosition:Long) {
        playerRepository.seekToPosition(seekPosition)
    }

    fun getCurrentMediaDuration():Long {
        return playerRepository.getDuration()
    }

    fun getCurrentPlaylist():List<Song> {
        return playerRepository.getMediaList()
    }

    fun stopPlayer() {
        playerRepository.stop()
        playerRepository.release()
    }
}