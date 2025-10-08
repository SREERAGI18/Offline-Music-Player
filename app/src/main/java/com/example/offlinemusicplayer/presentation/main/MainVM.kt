package com.example.offlinemusicplayer.presentation.main

import androidx.lifecycle.ViewModel
import com.example.offlinemusicplayer.player.PlayerServiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainVM @Inject constructor(
    private val playerRepository: PlayerServiceRepository
): ViewModel (){

    val currentMedia = playerRepository.currentMedia

    val playerState = playerRepository.currentState

    val currentMediaPosition = playerRepository.mediaPosition

    fun play() {
        playerRepository.play()
    }

    fun pause() {
        playerRepository.pause()
    }
}