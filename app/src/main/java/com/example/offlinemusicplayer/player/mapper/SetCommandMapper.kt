package com.example.offlinemusicplayer.player.mapper

import androidx.media3.common.Player
import com.example.offlinemusicplayer.domain.enum_classes.Command

object SetCommandMapper {

    fun map(commands: Player.Commands): Set<Command> = buildSet {
        for (i in 0 until commands.size()) {
            try {
                add(CommandMapper.map(commands.get(i)))
            } catch (e: IllegalArgumentException) {
                // no action needed, command is not yet mapped into our domain.
            }
        }
    }
}