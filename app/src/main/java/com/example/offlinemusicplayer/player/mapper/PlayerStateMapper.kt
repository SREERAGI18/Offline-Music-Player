package com.example.offlinemusicplayer.player.mapper

import androidx.media3.common.Player
import com.example.offlinemusicplayer.domain.model.PlayerState

object PlayerStateMapper {
    fun map(player: Player): PlayerState {
        return if ((
                    player.playbackState == Player.STATE_BUFFERING ||
                            player.playbackState == Player.STATE_READY
                    ) &&
            player.playWhenReady
        ) {
            PlayerState.Playing
        } else if (player.isLoading) {
            PlayerState.Loading
        } else {
            map(player.playbackState)
        }
    }

    private fun map(@Player.State media3PlayerState: Int): PlayerState = when (media3PlayerState) {
        Player.STATE_IDLE -> PlayerState.Idle
        Player.STATE_BUFFERING -> PlayerState.Loading
        Player.STATE_READY -> PlayerState.Ready
        Player.STATE_ENDED -> PlayerState.Ended
        else -> throw IllegalArgumentException("Invalid media3 player state: $media3PlayerState")
    }
}