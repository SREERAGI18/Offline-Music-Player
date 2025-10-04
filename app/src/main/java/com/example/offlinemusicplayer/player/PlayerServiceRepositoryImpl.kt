package com.example.offlinemusicplayer.player

import android.net.Uri
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import com.example.offlinemusicplayer.data.local.entity.SongsEntity
import com.example.offlinemusicplayer.domain.model.Command
import com.example.offlinemusicplayer.domain.model.PlayerState
import com.example.offlinemusicplayer.player.mapper.MediaMapper
import com.example.offlinemusicplayer.player.mapper.PlayerStateMapper
import com.example.offlinemusicplayer.player.mapper.SetCommandMapper
import com.github.difflib.DiffUtils
import com.github.difflib.patch.DeltaType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.Closeable
import java.io.File
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class PlayerServiceRepositoryImpl @Inject constructor(
    private val mediaMapper: MediaMapper,
    private val coroutineScope: CoroutineScope,
): PlayerServiceRepository, Closeable {

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
    private var _currentMedia = MutableStateFlow<SongsEntity?>(null)
    override val currentMedia: StateFlow<SongsEntity?> get() = _currentMedia

    private var _shuffleModeEnabled = MutableStateFlow(false)
    override val shuffleModeEnabled: StateFlow<Boolean> get() = _shuffleModeEnabled

    /**
     * The current playback speed relative to 1.0.
     */
    private var _playbackSpeed = MutableStateFlow(1f)

    private var _seekBackIncrement = MutableStateFlow<Duration?>(null)
    override val seekBackIncrement: StateFlow<Duration?> get() = _seekBackIncrement

    private var _seekForwardIncrement = MutableStateFlow<Duration?>(null)
    override val seekForwardIncrement: StateFlow<Duration?> get() = _seekForwardIncrement

    private var mediaIndexToSeekTo: Int? = null

    private val playingMediaFlow: MutableStateFlow<List<SongsEntity>> = MutableStateFlow(listOf())

    init {
        observePlaylist()
        observeSeekPosition()
    }

    private val listener = object : Player.Listener {
        private val eventHandlers = mapOf(
            Player.EVENT_AVAILABLE_COMMANDS_CHANGED to ::updateAvailableCommands,
            Player.EVENT_MEDIA_ITEM_TRANSITION to ::updateCurrentMediaItem,
            Player.EVENT_SHUFFLE_MODE_ENABLED_CHANGED to ::updateShuffleMode,
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
            Player.EVENT_PLAY_WHEN_READY_CHANGED to ::updateState
        )

        override fun onEvents(player: Player, events: Player.Events) {
            val called = mutableSetOf<(Player) -> Unit>()
            for ((event, handler) in eventHandlers) {
                if (events.contains(event) && !called.contains(handler)) {
                    handler.invoke(player)
                    called.add(handler)
                }
            }
        }

        override fun onTimelineChanged(timeline: Timeline, reason: Int) {
            updateTimeline(player.value!!)
        }

        override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
            super.onMediaMetadataChanged(mediaMetadata)
            val currentMediaItemIndex = _player.value?.currentMediaItemIndex ?:0
            if (currentMediaItemIndex >= playingMediaFlow.value.size) {
                // Bug when app gets restarted
                Log.e(TAG, "ErrorCheck: Index($currentMediaItemIndex) >= ListSize(${playingMediaFlow.value.size})")
                // Even after using fallback if this check is not satisfied then return
                if (currentMediaItemIndex >= playingMediaFlow.value.size) {
                    Log.e(TAG, "ErrorResult: Index($currentMediaItemIndex) >= ListSize(${playingMediaFlow.value.size})")
                    return
                }
            }
        }


    }

    private fun updatePlaybackSpeed(player: Player) {
        _playbackSpeed.value = player.playbackParameters.speed
    }

    private fun updateShuffleMode(player: Player) {
        _shuffleModeEnabled.value = player.shuffleModeEnabled
    }

    private fun updateCurrentMediaItem(player: Player) {
        _currentMedia.value = player.currentMediaItem?.let {
            mediaMapper.map(it)
        }
        updatePosition()
    }

    /**
     * Update the state based on [Player.isPlaying], [Player.isLoading],
     * [Player.getPlaybackState] and [Player.getPlayWhenReady] properties.
     */
    private fun updateState(player: Player) {
        _currentState.value = PlayerStateMapper.map(player)

        Log.e(TAG, "Player state changed to ${_currentState.value}")
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
            player.seekTo(index, 0)
            player.prepare()
            player.play()
            mediaIndexToSeekTo = null
        }
    }

    /**
     * Connect this repository to the player including listening to events.
     */
    fun connect(player: Player, onClose: () -> Unit) {
        // TODO support a cycle of changing players

        checkNotClosed()

        if (this.onClose != null) {
            throw IllegalStateException("previously connected")
        }

        _player.value = player
        player.prepare()
        _connected.value = true
        player.addListener(listener)

        updateCurrentMediaItem(player)
        updateAvailableCommands(player)
        updateShuffleMode(player)
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
        }
    }

    override fun seekForward() {
        checkNotClosed()

        player.value?.let {
            it.seekForward()
            updatePosition()
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
    }

    /**
     * This operation will stop the current MediaItem that is playing, if there is one, as per
     * [Player.setMediaItem].
     */
    override fun setMedia(media: SongsEntity) {
        checkNotClosed()

        player.value?.let {
            it.setMediaItem(getMediaItemFromSong(media))
            updatePosition()
        }
    }

    private fun getMediaItemFromSong(song: SongsEntity): MediaItem {
        return  MediaItem.fromUri(
            Uri.fromFile(
                File(song.path)
            )
        )
    }

    /**
     * This operation will stop the current [MediaItem] that is playing, if there is one, as per
     * [Player.setMediaItems].
     */
    override fun setMediaList(mediaList: List<SongsEntity>) {
        checkNotClosed()

        playingMediaFlow.value = mediaList
    }

    override fun getMediaList(): List<SongsEntity> {
        checkNotClosed()

        return playingMediaFlow.value
    }

    override fun setMediaList(mediaList: List<SongsEntity>, index: Int, position: Duration?) {
        checkNotClosed()

        playingMediaFlow.value = mediaList
        mediaIndexToSeekTo = index
    }

    override fun addMedia(media: SongsEntity) {
        checkNotClosed()

        player.value?.addMediaItem(getMediaItemFromSong(media))
    }

    override fun addMedia(index: Int, media: SongsEntity) {
        checkNotClosed()

        player.value?.addMediaItem(index, getMediaItemFromSong(media))
    }

    override fun removeMedia(index: Int) {
        checkNotClosed()

        player.value?.removeMediaItem(index)
    }

    override fun clearMediaList() {
        checkNotClosed()

        player.value?.clearMediaItems()
    }

    override fun getMediaCount(): Int {
        checkNotClosed()

        return player.value?.mediaItemCount ?: 0
    }

    override fun getMediaAt(index: Int): SongsEntity? {
        checkNotClosed()

        return player.value?.getMediaItemAt(index)?.let {
            mediaMapper.map(it)
        }
    }

    override fun getCurrentMediaIndex(): Int {
        checkNotClosed()
        return player.value?.currentMediaItemIndex ?: 0
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

    private fun observePlaylist() {
        coroutineScope.launch {
            var oldItems:List<SongsEntity> = emptyList()
            playingMediaFlow.collect {
                val newItems = it
                val patches = withContext(Dispatchers.IO) {
                    DiffUtils.diff(oldItems, newItems) { oldItem, newItem ->
                        oldItem.id == newItem.id && oldItem.path==newItem.path
                    }
                }
                withContext(Dispatchers.Main) {
                    _player.value?.let { player ->
                        if (patches.deltas.size == 1 && patches.deltas.firstOrNull()?.type == DeltaType.INSERT && player.mediaItemCount == 0) {
                            Log.e(TAG, "DeltaType.INSERT all")
                            player.setMediaItems(
                                patches.deltas.first().target.lines.map { song ->
                                    getMediaItemFromSong(song)
                                }
                            )
                        } else {
                            patches.deltas.forEach { delta ->
                                when(delta.type) {
                                    DeltaType.DELETE -> {
                                        player.removeMediaItems(
                                            delta.target.position,
                                            delta.target.position + delta.source.lines.size
                                        )
                                        Log.e(TAG, "DeltaType.DELETE")
                                    }
                                    DeltaType.INSERT -> {
                                        player.addMediaItems(
                                            delta.target.position,
                                            delta.target.lines.map { song ->
                                                getMediaItemFromSong(song)
                                            }
                                        )
                                        Log.e(TAG, "DeltaType.INSERT")
                                    }
                                    DeltaType.CHANGE -> {
                                        player.removeMediaItems(
                                            delta.target.position,
                                            delta.target.position + delta.source.lines.size
                                        )
                                        player.addMediaItems(
                                            delta.target.position,
                                            delta.target.lines.map { song ->
                                                getMediaItemFromSong(song)
                                            }
                                        )
                                        Log.e(TAG, "DeltaType.CHANGE")
                                    }
                                    DeltaType.EQUAL -> {
                                        // Ignore
                                    }
                                    null -> {
                                        // Ignore
                                    }
                                }
                            }
                        }
                    }
                    updatePosition()
                    if (oldItems.isEmpty() && newItems.isNotEmpty()) {
                        prepare()
                    }
                }
                Log.e(TAG, "CurrentPlayingMediaList: ${newItems.map {med ->
                    med.artist + "\n"
                }}")
                oldItems = newItems
            }
        }
    }

    private fun observeSeekPosition() {
        coroutineScope.launch(Dispatchers.Main) {
            while (isActive) {
                updatePosition()
                delay(1000)
            }
        }
    }

    override fun getDuration(): Long {
        return player.value?.duration ?: 0L
    }
}