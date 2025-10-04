package com.example.offlinemusicplayer.player.mapper

import androidx.media3.common.Player
import androidx.media3.common.Player.COMMAND_PLAY_PAUSE
import androidx.media3.common.Player.COMMAND_SEEK_BACK
import androidx.media3.common.Player.COMMAND_SEEK_FORWARD
import androidx.media3.common.Player.COMMAND_SEEK_TO_NEXT_MEDIA_ITEM
import androidx.media3.common.Player.COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM
import androidx.media3.common.Player.COMMAND_SET_SHUFFLE_MODE
import com.example.offlinemusicplayer.domain.model.Command

object CommandMapper {

    fun map(@Player.Command command: Int): Command =
        when (command) {
            COMMAND_PLAY_PAUSE -> Command.PlayPause
            COMMAND_SEEK_BACK -> Command.SeekBack
            COMMAND_SEEK_FORWARD -> Command.SeekForward
            COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM -> Command.SkipToPreviousMedia
            COMMAND_SEEK_TO_NEXT_MEDIA_ITEM -> Command.SkipToNextMedia
            COMMAND_SET_SHUFFLE_MODE -> Command.SetShuffle
            else -> throw IllegalArgumentException("Invalid command: $command")
        }
}