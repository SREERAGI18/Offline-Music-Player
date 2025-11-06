package com.lyrisync.player.mapper

import androidx.media3.common.Player
import com.lyrisync.domain.enum_classes.RepeatMode

/**
 * Maps the Media3 Player's integer-based repeat mode to the domain's [RepeatMode] enum.
 */
object RepeatModeMapper {

    /**
     * Converts the repeat mode from the Media3 [Player] instance to a [RepeatMode] enum.
     *
     * @param player The Media3 player instance.
     * @return The corresponding [RepeatMode] (ONE, ALL, or OFF).
     */
    fun map(player: Player): RepeatMode {
        return when (player.repeatMode) {
            Player.REPEAT_MODE_ONE -> RepeatMode.ONE
            Player.REPEAT_MODE_OFF -> RepeatMode.OFF
            else -> RepeatMode.ALL
        }
    }

    fun map(repeatMode: RepeatMode): @Player.RepeatMode Int {
        return when (repeatMode) {
            RepeatMode.ONE -> Player.REPEAT_MODE_ONE
            RepeatMode.OFF -> Player.REPEAT_MODE_OFF
            RepeatMode.ALL -> Player.REPEAT_MODE_ALL
        }
    }
}
