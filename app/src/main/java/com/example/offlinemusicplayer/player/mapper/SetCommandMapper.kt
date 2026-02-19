package com.example.offlinemusicplayer.player.mapper

import androidx.media3.common.Player
import com.example.offlinemusicplayer.domain.enumclasses.Command
import com.example.offlinemusicplayer.util.Logger

object SetCommandMapper {
    fun map(commands: Player.Commands): Set<Command> =
        buildSet {
            for (i in 0 until commands.size()) {
                try {
                    add(CommandMapper.map(commands.get(i)))
                } catch (e: IllegalArgumentException) {
                    Logger.logDebug(
                        "SetCommandMapper",
                        "Ignoring unmapped command: ${commands.get(i)}, ${e.message}",
                    )
                }
            }
        }
}
