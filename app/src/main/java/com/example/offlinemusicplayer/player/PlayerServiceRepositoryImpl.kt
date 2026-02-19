package com.example.offlinemusicplayer.player

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.datasource.HttpDataSource
import com.example.offlinemusicplayer.domain.enumclasses.Command
import com.example.offlinemusicplayer.domain.enumclasses.PlayerState
import com.example.offlinemusicplayer.domain.enumclasses.RepeatMode
import com.example.offlinemusicplayer.domain.model.Song
import com.example.offlinemusicplayer.domain.usecase.songs.IncrementPlayCount
import com.example.offlinemusicplayer.player.mapper.MediaMapper
import com.example.offlinemusicplayer.player.mapper.PlayerStateMapper
import com.example.offlinemusicplayer.player.mapper.RepeatModeMapper
import com.example.offlinemusicplayer.player.mapper.SetCommandMapper
import com.example.offlinemusicplayer.util.Constants.ONE_SEC_IN_MS
import com.example.offlinemusicplayer.util.Logger
import com.example.offlinemusicplayer.util.PreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.Closeable
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class PlayerServiceRepositoryImpl
    @Inject
    constructor(
        private val mediaMapper: MediaMapper,
        private val coroutineScope: CoroutineScope,
        private val preferencesManager: PreferencesManager,
        private val incrementPlayCount: IncrementPlayCount,
    ) : PlayerServiceRepository,
        Closeable {
        private companion object {
            private val TAG = PlayerServiceRepositoryImpl::class.java.simpleName
        }

        private var onClose: (() -> Unit)? = null
        private var closed = false

        /**
         * The active player, or null if no active player is currently available.
         */
        private var _player = MutableStateFlow<Player?>(null)
        override val player: StateFlow<Player?> get() = _player

        private val _mediaPosition = MutableStateFlow<Long?>(0L)
        override val mediaPosition: StateFlow<Long?> get() = _mediaPosition

        private val _connected = MutableStateFlow(false)
        override val connected: StateFlow<Boolean> get() = _connected

        private val _availableCommands = MutableStateFlow(emptySet<Command>())
        override val availableCommands: StateFlow<Set<Command>> get() = _availableCommands

        private val _currentState = MutableStateFlow(PlayerState.Idle)
        override val currentState: StateFlow<PlayerState> get() = _currentState

        /**
         * The current media playing, or that would play when user hit play.
         */
        private var _currentMedia = MutableStateFlow<Song?>(null)
        override val currentMedia: StateFlow<Song?> get() = _currentMedia

        private var _currentMediaIndex = MutableStateFlow<Int?>(null)
        override val currentMediaIndex: StateFlow<Int?> get() = _currentMediaIndex

        private var _shuffleModeEnabled = MutableStateFlow(false)
        override val shuffleModeEnabled: StateFlow<Boolean> get() = _shuffleModeEnabled

        private var _repeatMode = MutableStateFlow(RepeatMode.ALL)
        override val repeatMode: StateFlow<RepeatMode> get() = _repeatMode

        private var _playbackSpeed = MutableStateFlow(1f)
        override val playbackSpeed: StateFlow<Float> get() = _playbackSpeed

        private var _seekBackIncrement = MutableStateFlow<Duration?>(null)
        override val seekBackIncrement: StateFlow<Duration?> get() = _seekBackIncrement

        private var _seekForwardIncrement = MutableStateFlow<Duration?>(null)
        override val seekForwardIncrement: StateFlow<Duration?> get() = _seekForwardIncrement

        private var mediaIndexToSeekTo: Int? = null
        private var pendingLastPlayedSongIncrementId: Long? = null

        private var isLastPlayedInitialised = false

        init {
            observeSeekPosition()
        }

        private val listener =
            object : Player.Listener {
                private val eventHandlers =
                    mapOf(
                        Player.EVENT_AVAILABLE_COMMANDS_CHANGED to ::updateAvailableCommands,
                        Player.EVENT_MEDIA_ITEM_TRANSITION to ::updateCurrentMediaItem,
                        Player.EVENT_SHUFFLE_MODE_ENABLED_CHANGED to ::updateShuffleMode,
                        Player.EVENT_REPEAT_MODE_CHANGED to ::updateRepeatMode,
                        Player.EVENT_PLAYBACK_PARAMETERS_CHANGED to ::updatePlaybackSpeed,
                        Player.EVENT_SEEK_BACK_INCREMENT_CHANGED to ::updateSeekBackIncrement,
                        Player.EVENT_SEEK_FORWARD_INCREMENT_CHANGED to ::updateSeekForwardIncrement,
                        // Player.EVENT_TIMELINE_CHANGED to ::updateTimeline,
                        // Reason for handling these events here, instead of using individual callbacks
                        // (onIsLoadingChanged, onIsPlayingChanged, onPlaybackStateChanged, etc):
                        // - The listener intends to use multiple state values that are reported through
                        //   separate callbacks together, or in combination with Player getter methods
                        // Reference:
                        // https://exoplayer.dev/listening-to-player-events.html#individual-callbacks-vs-onevents
                        Player.EVENT_IS_LOADING_CHANGED to ::updateState,
                        Player.EVENT_IS_PLAYING_CHANGED to ::updateState,
                        Player.EVENT_PLAYBACK_STATE_CHANGED to ::updateState,
                        Player.EVENT_PLAY_WHEN_READY_CHANGED to ::updateState,
                    )

                override fun onEvents(
                    player: Player,
                    events: Player.Events,
                ) {
                    val called = mutableSetOf<(Player) -> Unit>()
                    for ((event, handler) in eventHandlers) {
                        if (events.contains(event) && !called.contains(handler)) {
                            handler.invoke(player)
                            called.add(handler)
                        }
                    }
                }

                override fun onTimelineChanged(
                    timeline: Timeline,
                    reason: Int,
                ) {
                    if (reason == Player.TIMELINE_CHANGE_REASON_PLAYLIST_CHANGED && !isLastPlayedInitialised) {
                        initialiseLastPlayedSongIfExist()
                        isLastPlayedInitialised = true
                    }
                    updateTimeline(player.value!!)
                }

                override fun onMediaItemTransition(
                    mediaItem: MediaItem?,
                    reason: Int,
                ) {
                    super.onMediaItemTransition(mediaItem, reason)
                    Logger.logInfo(TAG, "onMediaItemTransition, Reason: $reason")
                    if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED || mediaItem == null) return

                    coroutineScope.launch {
                        val songId = mediaItem.mediaId.toLong()
                        incrementPlayCount(songId)
                    }
                }

                override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                    super.onMediaMetadataChanged(mediaMetadata)
                    Logger.logInfo(TAG, "onMediaMetadataChanged")
                }

                override fun onPlayerError(error: PlaybackException) {
                    val cause = error.cause
                    if (cause is HttpDataSource.HttpDataSourceException) {
                        // An HTTP error occurred
                        val httpError = cause
                        if (httpError is HttpDataSource.InvalidResponseCodeException) {
                            // Specific HTTP error with response code
                            val responseCode = httpError.responseCode
                            // Log or display the error details
                            Logger.logError(tag = TAG, message = "HTTP Error: Response code $responseCode")
                        } else {
                            // Other HTTP error
                            Logger.logError(tag = TAG, message = "HTTP DataSource Error: ${httpError.message}")
                        }
                    } else {
                        // Handle other types of PlaybackException
                        Logger.logError(tag = TAG, message = "Playback Error: ${error.message}")
                    }
                }
            }

        fun initialiseLastPlayedSongIfExist() {
            val lastPlayedSongId = preferencesManager.getLastPlayedSongId()

            val repeatMode = preferencesManager.getRepeatMode()
            val shuffleModeEnabled = preferencesManager.getShuffleMode()

            if (lastPlayedSongId != 0L) {
                val mediaList = getMediaList()
                val lastPlayedMediaIndex = mediaList.indexOfFirst { it.id == lastPlayedSongId }
                mediaIndexToSeekTo = lastPlayedMediaIndex
                pendingLastPlayedSongIncrementId = lastPlayedSongId
                setRepeatMode(repeatMode)
                setShuffleModeEnabled(shuffleModeEnabled)
//            seekToPosition(lastPlayedPosition)
            }
        }

        private fun updatePlaybackSpeed(player: Player) {
            _playbackSpeed.value = player.playbackParameters.speed
        }

        private fun updateShuffleMode(player: Player) {
            _shuffleModeEnabled.value = player.shuffleModeEnabled
        }

        private fun updateRepeatMode(player: Player) {
            _repeatMode.value = RepeatModeMapper.map(player)
        }

        private fun updateCurrentMediaItem(player: Player) {
            val song =
                player.currentMediaItem?.let {
                    mediaMapper.mapToSong(it)
                }
            _currentMedia.value = song
            _currentMediaIndex.value = player.currentMediaItemIndex
            if (song != null) {
                preferencesManager.setLastPlayedSong(song.id)
            }
            updatePosition()
        }

        /**
         * Update the state based on [Player.isPlaying], [Player.isLoading],
         * [Player.getPlaybackState] and [Player.getPlayWhenReady] properties.
         */
        private fun updateState(player: Player) {
            val playerState = PlayerStateMapper.map(player)
            _currentState.value = playerState

            pendingLastPlayedSongIncrementId?.let { songId ->
                if (songId.toString() == player.currentMediaItem?.mediaId && playerState == PlayerState.Playing) {
                    coroutineScope.launch {
                        incrementPlayCount(songId)
                    }
                    pendingLastPlayedSongIncrementId = null
                }
            }

            Logger.logError(TAG, "Player state changed to ${_currentState.value}")
        }

        private fun updateAvailableCommands(player: Player) {
            player.availableCommands.let {
                _availableCommands.value = SetCommandMapper.map(it)
            }
        }

        private fun updateSeekBackIncrement(player: Player) {
            _seekBackIncrement.value = player.seekBackIncrement.toDuration(DurationUnit.MILLISECONDS)
        }

        private fun updateSeekForwardIncrement(player: Player) {
            _seekForwardIncrement.value = player.seekForwardIncrement.toDuration(DurationUnit.MILLISECONDS)
        }

        private fun updateTimeline(player: Player) {
            mediaIndexToSeekTo?.let { index ->
                if (index < player.mediaItemCount) {
                    player.seekTo(index, 0)
                    player.prepare()
                    mediaIndexToSeekTo = null
                }
            }
        }

        /**
         * Connect this repository to the player including listening to events.
         */
        fun connect(
            player: Player,
            onClose: () -> Unit,
        ) {
            // TODO support a cycle of changing players

            checkNotClosed()

            check(this.onClose == null) { "previously connected" }

            _player.value = player
            _connected.value = true
            player.addListener(listener)

            updateCurrentMediaItem(player)
            updateAvailableCommands(player)
            updateShuffleMode(player)
            updateRepeatMode(player)
            updateState(player)
            updatePlaybackSpeed(player)
            updateSeekBackIncrement(player)
            updateSeekForwardIncrement(player)

            this.onClose = onClose
        }

        /**
         * Close this repository and release the listener from the player.
         */
        override fun close() {
            closed = true

            // TODO consider ordering for UI updates purposes
            _player.value?.removeListener(listener)
            onClose?.invoke()
            _connected.value = false
        }

        override fun seekToDefaultPosition(mediaIndex: Int) {
            checkNotClosed()

            _player.value?.seekToDefaultPosition(mediaIndex)
        }

        override fun prepare() {
            checkNotClosed()

            _player.value?.prepare()
        }

        override fun play() {
            checkNotClosed()

            _player.value?.let {
                it.play()
                updatePosition()
            }
        }

        override fun pause() {
            checkNotClosed()

            player.value?.let {
                it.pause()
                updatePosition()
            }
        }

        override fun stop() {
            checkNotClosed()

            player.value?.let {
                it.stop()
                setMediaList(emptyList())
            }
        }

        override fun hasPreviousMedia(): Boolean {
            checkNotClosed()

            return player.value?.hasPreviousMediaItem() ?: false
        }

        override fun skipToPreviousMedia() {
            checkNotClosed()

            player.value?.let {
                it.seekToPreviousMediaItem()
                updatePosition()
            }
        }

        override fun skipToMediaByIndex(index: Int) {
            checkNotClosed()

            player.value?.let {
                it.seekTo(index, 0L)
                updatePosition()
            }
        }

        override fun hasNextMedia(): Boolean {
            checkNotClosed()

            return player.value?.hasNextMediaItem() ?: false
        }

        override fun skipToNextMedia() {
            checkNotClosed()

            player.value?.let {
                it.seekToNextMediaItem()
                updatePosition()
            }
        }

        override fun seekBack() {
            checkNotClosed()

            player.value?.let {
                it.seekBack()
                updatePosition()
                saveLastPlayedPosition()
            }
        }

        override fun seekForward() {
            checkNotClosed()

            player.value?.let {
                it.seekForward()
                updatePosition()
                saveLastPlayedPosition()
            }
        }

        override fun seekToPosition(seekPosition: Long) {
            checkNotClosed()

            player.value?.let {
                it.seekTo(seekPosition)
                updatePosition()
            }
        }

        override fun setShuffleModeEnabled(shuffleModeEnabled: Boolean) {
            checkNotClosed()

            player.value?.shuffleModeEnabled = shuffleModeEnabled
            preferencesManager.saveShuffleMode(shuffleModeEnabled)
        }

        override fun setRepeatMode(repeatMode: RepeatMode) {
            checkNotClosed()

            player.value?.repeatMode = RepeatModeMapper.map(repeatMode)
            preferencesManager.saveRepeatMode(repeatMode)
        }

        /**
         * This operation will stop the current MediaItem that is playing, if there is one, as per
         * [Player.setMediaItem].
         */
        override fun setMedia(media: Song) {
            checkNotClosed()

            player.value?.let {
                it.setMediaItem(mediaMapper.mapToMediaItem(media))
                updatePosition()
            }
        }

        /**
         * This operation will stop the current [MediaItem] that is playing, if there is one, as per
         * [Player.setMediaItems].
         */
        override fun setMediaList(mediaList: List<Song>) {
            checkNotClosed()

            if (isSamePlaylist(mediaList)) return

            _player.value?.let { player ->
                player.clearMediaItems()
                val mediaItems = mediaList.map { mediaMapper.mapToMediaItem(it) }
                player.setMediaItems(mediaItems)
                prepare()
            }
            updatePosition()
        }

        override fun getMediaList(): List<Song> {
            checkNotClosed()

            val player = this.player.value ?: return emptyList()

            return List(player.mediaItemCount) { index ->
                mediaMapper.mapToSong(player.getMediaItemAt(index))
            }
        }

        override fun setMediaList(
            mediaList: List<Song>,
            index: Int,
            position: Duration?,
        ) {
            checkNotClosed()

            if (isSamePlaylist(mediaList)) return

            mediaIndexToSeekTo = index
            _player.value?.let { player ->
                player.clearMediaItems()
                val mediaItems = mediaList.map { mediaMapper.mapToMediaItem(it) }
                player.setMediaItems(mediaItems)
                prepare()
            }
        }

        /**
         * Efficiently checks if the provided list of songs is the same as the one
         * currently loaded in the player.
         *
         * @param newList The new list of songs to compare.
         * @return `true` if the playlists are identical (same songs in the same order),
         *         `false` otherwise.
         */
        private fun isSamePlaylist(newList: List<Song>): Boolean {
            val player = _player.value ?: return false
            val currentListSize = player.mediaItemCount

            // 1. Quick check: If sizes are different, they can't be the same list.
            if (newList.size != currentListSize) {
                return false
            }

            // 2. If both are empty, they are the same.
            if (newList.isEmpty()) {
                return true
            }

            // 3. Compare song IDs in order. The `mediaId` in a MediaItem corresponds to our song ID.
            for (i in newList.indices) {
                val newSongId = newList[i].id.toString()
                val currentMediaId = player.getMediaItemAt(i).mediaId
                if (newSongId != currentMediaId) {
                    return false
                }
            }

            // If all checks pass, the playlists are the same.
            return true
        }

        override fun addMedia(media: Song) {
            checkNotClosed()

            player.value?.addMediaItem(mediaMapper.mapToMediaItem(media))
        }

        override fun addMedia(mediaList: List<Song>) {
            player.value?.addMediaItems(
                mediaList.map {
                    mediaMapper.mapToMediaItem(it)
                },
            )
        }

        override fun addMedia(
            index: Int,
            media: Song,
        ) {
            checkNotClosed()

            player.value?.addMediaItem(index, mediaMapper.mapToMediaItem(media))
        }

        override fun removeMedia(index: Int) {
            checkNotClosed()

            player.value?.removeMediaItem(index)
        }

        override fun moveMedia(
            fromIndex: Int,
            toIndex: Int,
        ) {
            checkNotClosed()

            player.value?.moveMediaItem(fromIndex, toIndex)
        }

        override fun clearMediaList() {
            checkNotClosed()

            player.value?.clearMediaItems()
        }

        override fun getMediaCount(): Int {
            checkNotClosed()

            return player.value?.mediaItemCount ?: 0
        }

        override fun getMediaAt(index: Int): Song? {
            checkNotClosed()

            return player.value?.getMediaItemAt(index)?.let {
                mediaMapper.mapToSong(it)
            }
        }

        override fun getCurrentMediaIndex(): Int {
            checkNotClosed()
            return player.value?.currentMediaItemIndex ?: 0
        }

        override fun findIndexOfSongInPlaylist(songId: Long?): Int? {
            checkNotClosed()

            val songs = getMediaList()

            val songIndex = songs.indexOfFirst { it.id == songId }

            return if (songIndex != -1) songIndex else null
        }

        override fun release() {
            checkNotClosed()
            player.value?.release()
        }

        /**
         * Update the position to show track progress correctly on screen.
         * Updating roughly once a second while activity is foregrounded is appropriate.
         */
        private fun updatePosition() {
            _mediaPosition.value = player.value?.currentPosition
        }

        override fun setPlaybackSpeed(speed: Float) {
            player.value?.setPlaybackSpeed(speed)
        }

        private fun checkNotClosed() {
            check(!closed) { "Player is already closed." }
        }

        private fun observeSeekPosition() {
            coroutineScope.launch(Dispatchers.Main) {
                while (isActive) {
                    updatePosition()
                    saveLastPlayedPosition()
                    delay(ONE_SEC_IN_MS)
                }
            }
        }

        private fun saveLastPlayedPosition() {
            val currentPosition = player.value?.currentPosition
            preferencesManager.setLastPlayedPosition(currentPosition ?: 0L)
        }

        override fun getDuration(): Long = player.value?.duration ?: 0L
    }
