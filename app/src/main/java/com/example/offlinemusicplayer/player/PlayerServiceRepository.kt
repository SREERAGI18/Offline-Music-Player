package com.example.offlinemusicplayer.player

import androidx.media3.common.Player
import com.example.offlinemusicplayer.data.local.entity.SongsEntity
import com.example.offlinemusicplayer.domain.model.Command
import com.example.offlinemusicplayer.domain.model.PlayerState
import kotlinx.coroutines.flow.StateFlow
import kotlin.time.Duration

interface PlayerServiceRepository {

    /**
     * The active player, or null if no active player is currently available.
     */
    val player: StateFlow<Player?>

    /**
     * Returns whether the repository is currently connected to a working Player.
     */
    val connected: StateFlow<Boolean>

    /**
     * Returns the player's currently available [commands][Command].
     */
    val availableCommands: StateFlow<Set<Command>>

    /**
     * Returns the player's current [state][PlayerState].
     */
    val currentState: StateFlow<PlayerState>

    /**
     * Returns the current [SongsEntity] playing, or that would play when player starts playing.
     */
    val currentMedia: StateFlow<SongsEntity?>

    val mediaPosition: StateFlow<Long?>

    /**
     * Returns the current value for shuffling of [SongsEntity] mode.
     */
    val shuffleModeEnabled: StateFlow<Boolean>

    /**
     * Returns the [seekBack] increment.
     */
    val seekBackIncrement: StateFlow<Duration?>

    /**
     * Returns the [seekForward] increment.
     */
    val seekForwardIncrement: StateFlow<Duration?>

    /**
     * Prepares the player. E.g. player will start acquiring all the required resources to play.
     */
    fun prepare()

    /**
     * Resumes playback as soon as player is ready.
     */
    fun play()

    /**
     * Seeks to the default position associated with the specified Media.
     */
    fun seekToDefaultPosition(mediaIndex: Int)

    /**
     * Pauses playback.
     */
    fun pause()

    /**
     * stops playback.
     */
    fun stop()

    /**
     * Returns whether a previous [SongsEntity] exists.
     */
    fun hasPreviousMedia(): Boolean

    /**
     * Skips to the default position of previous [SongsEntity].
     */
    fun skipToPreviousMedia()

    /**
     * Skips to specific position [Int] from the playlist [SongsEntity].
     */
    fun skipToMediaByIndex(index: Int)

    /**
     * Returns whether a next [SongsEntity] exists.
     */
    fun hasNextMedia(): Boolean

    /**
     * Skips to the default position of next [SongsEntity].
     */
    fun skipToNextMedia()

    /**
     * Seeks back in the [current media][currentMedia] by [seek back increment][getSeekBackIncrement].
     */
    fun seekBack()

    /**
     * Seek forward in the [current media][currentMedia] by [seek forward increment][getSeekForwardIncrement].
     */
    fun seekForward()

    /**
     * Moves the playback cursor to a specific position within the currently playing media.
     *
     * @param seekPosition The target position in milliseconds where the playback should move.
     */
    fun seekToPosition(seekPosition:Long)

    /**
     * Sets whether shuffling of [SongsEntity] is enabled.
     */
    fun setShuffleModeEnabled(shuffleModeEnabled: Boolean)

    /**
     * Clears the playlist, adds the specified [SongsEntity] and resets the position to
     * the default position.
     */
    fun setMedia(media: SongsEntity)

    /**
     * Clears the playlist, adds the specified [SongsEntity] list and resets the position to
     * the default position.
     *
     * @param mediaList The new [SongsEntity] list.
     */
    fun setMediaList(mediaList: List<SongsEntity>)

    /**
     * Clears the playlist, adds the specified [SongsEntity] list and resets the position to
     * the provided position.
     *
     * @param mediaList The new [SongsEntity] list.
     * @param index The [SongsEntity] index to start playback from
     * @param position The position to start playback from.
     */

    fun setMediaList(mediaList: List<SongsEntity>, index: Int, position: Duration? = null)

    /**
     * returns a list of current playing playlist.
     */
    fun getMediaList():List<SongsEntity>

    /**
     * Adds a [SongsEntity] to the end of the playlist.
     */
    fun addMedia(media: SongsEntity)

    /**
     * Adds a [SongsEntity] at the given index of the playlist.
     *
     * @param index The index at which to add the [SongsEntity]. If the index is larger than the size
     * of the playlist, the media is added to the end of the playlist.
     * @param media The [SongsEntity] to add.
     */
    fun addMedia(index: Int, media: SongsEntity)

    /**
     * Removes the [SongsEntity] at the given index of the playlist.
     *
     * @param index The index at which to remove the [SongsEntity].
     */
    fun removeMedia(index: Int)

    /**
     * Clears the playlist.
     */
    fun clearMediaList()

    /**
     * Returns the number of [SongsEntity] in the playlist.
     */
    fun getMediaCount(): Int

    /**
     * Returns the [SongsEntity] at the given index.
     */
    fun getMediaAt(index: Int): SongsEntity?

    /**
     * Returns the index of the current [SongsEntity].
     */
    fun getCurrentMediaIndex(): Int

    /**
     * Releases the player. This method must be called when the player is no longer required. The
     * player must not be used after calling this method.
     */
    fun release()

    /**
     * Set the playback speed.
     */
    fun setPlaybackSpeed(speed: Float)

    /**
     * Get current media duration.
     */
    fun getDuration():Long
}