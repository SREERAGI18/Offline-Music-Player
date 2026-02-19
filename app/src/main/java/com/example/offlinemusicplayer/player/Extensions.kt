package com.example.offlinemusicplayer.player

import androidx.media3.common.Player
import androidx.media3.session.MediaController
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

fun MediaController.playbackStateFlow(): Flow<Int> =
    callbackFlow {
        val listener =
            object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    trySend(playbackState)
                }
            }
        addListener(listener)
        awaitClose { removeListener(listener) }
    }
