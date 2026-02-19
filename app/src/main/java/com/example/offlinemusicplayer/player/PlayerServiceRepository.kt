package com.example.offlinemusicplayer.player

import androidx.media3.common.Player
import com.example.offlinemusicplayer.domain.enumclasses.Command
import com.example.offlinemusicplayer.domain.enumclasses.PlayerState
import com.example.offlinemusicplayer.domain.enumclasses.RepeatMode
import com.example.offlinemusicplayer.domain.model.Song
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
     * Returns the current [Song] playing, or that would play when player starts playing.
     */
    val currentMedia: StateFlow<Song?>

    /**
     * Returns the current playing [Song] index in the current playlist.
     */
    val currentMediaIndex: StateFlow<Int?>

    val mediaPosition: StateFlow<Long?>

    /**
     * Returns the current value for shuffling of [Song] mode.
     */
    val shuffleModeEnabled: StateFlow<Boolean>

    /**
     * Returns the current value for repeat mode.
     */
    val repeatMode: StateFlow<RepeatMode>

    /**
     * Returns the [seekBack] increment.
     */
    val seekBackIncrement: StateFlow<Duration?>

    /**
     * The current playback speed relative to 1.0.
     */
    val playbackSpeed: StateFlow<Float>

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
     * Returns whether a previous [Song] exists.
     */
    fun hasPreviousMedia(): Boolean

    /**
     * Skips to the default position of previous [Song].
     */
    fun skipToPreviousMedia()

    /**
     * Skips to specific position [Int] from the playlist [Song].
     */
    fun skipToMediaByIndex(index: Int)

    /**
     * Returns whether a next [Song] exists.
     */
    fun hasNextMedia(): Boolean

    /**
     * Skips to the default position of next [Song].
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
    fun seekToPosition(seekPosition: Long)

    /**
     * Sets whether shuffling of [Song] is enabled.
     */
    fun setShuffleModeEnabled(shuffleModeEnabled: Boolean)

    /**
     * Sets the repeat mode.
     */
    fun setRepeatMode(repeatMode: RepeatMode)

    /**
     * Clears the playlist, adds the specified [Song] and resets the position to
     * the default position.
     */
    fun setMedia(media: Song)

    /**
     * Clears the playlist, adds the specified [Song] list and resets the position to
     * the default position.
     *
     * @param mediaList The new [Song] list.
     */
    fun setMediaList(mediaList: List<Song>)

    /**
     * Clears the playlist, adds the specified [Song] list and resets the position to
     * the provided position.
     *
     * @param mediaList The new [Song] list.
     * @param index The [Song] index to start playback from
     * @param position The position to start playback from.
     */

    fun setMediaList(
        mediaList: List<Song>,
        index: Int,
        position: Duration? = null,
    )

    /**
     * returns a list of current playing playlist.
     */
    fun getMediaList(): List<Song>

    /**
     * Adds a [Song] to the end of the playlist.
     */
    fun addMedia(media: Song)

    /**
     * Adds a list of [Song] to the end of the playlist.
     */
    fun addMedia(mediaList: List<Song>)

    /**
     * Adds a [Song] at the given index of the playlist.
     *
     * @param index The index at which to add the [Song]. If the index is larger than the size
     * of the playlist, the media is added to the end of the playlist.
     * @param media The [Song] to add.
     */
    fun addMedia(
        index: Int,
        media: Song,
    )

    /**
     * Removes the [Song] at the given index of the playlist.
     *
     * @param index The index at which to remove the [Song].
     */
    fun removeMedia(index: Int)

    /**
     * Moves the [Song] from its last position at the given position of the playlist.
     *
     * @param fromIndex The index from which the [Song] is moved.
     * @param toIndex The index to which the [Song] is moved.
     */
    fun moveMedia(
        fromIndex: Int,
        toIndex: Int,
    )

    /**
     * Clears the playlist.
     */
    fun clearMediaList()

    /**
     * Returns the number of [Song] in the playlist.
     */
    fun getMediaCount(): Int

    /**
     * Returns the [Song] at the given index.
     */
    fun getMediaAt(index: Int): Song?

    /**
     * Returns the index of the current [Song].
     */
    fun getCurrentMediaIndex(): Int

    /**
     * Returns the index of the [Song] in the playlist.
     * Returns null if the song is not found.
     *
     * @param songId The id of the [Song] to find.
     *
     */
    fun findIndexOfSongInPlaylist(songId: Long?): Int?

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
    fun getDuration(): Long
}
