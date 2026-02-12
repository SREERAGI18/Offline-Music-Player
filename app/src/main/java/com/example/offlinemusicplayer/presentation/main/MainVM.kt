package com.example.offlinemusicplayer.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.offlinemusicplayer.domain.model.Song
import com.example.offlinemusicplayer.domain.usecase.songs.GetSongsByIds
import com.example.offlinemusicplayer.domain.usecase.songs.SyncSongsWithDevice
import com.example.offlinemusicplayer.domain.usecase.songs.UpdateLyrics
import com.example.offlinemusicplayer.player.PlayerServiceRepository
import com.example.offlinemusicplayer.util.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class MainVM @Inject constructor(
    private val playerRepository: PlayerServiceRepository,
    private val syncSongsWithDevice: SyncSongsWithDevice,
    private val updateLyrics: UpdateLyrics,
    private val getSongsByIds: GetSongsByIds
) : ViewModel() {

    private val _newlyAddedSongCount = MutableStateFlow(0)
    val newlyAddedSongCount = _newlyAddedSongCount.asStateFlow()

    val currentMedia = playerRepository.currentMedia

    val playerState = playerRepository.currentState

    val currentMediaPosition = playerRepository.mediaPosition

    val shuffleModeEnabled = playerRepository.shuffleModeEnabled

    val repeatMode = playerRepository.repeatMode

    private val _lyrics = MutableStateFlow<Map<Long, String>>(emptyMap())
    val lyrics = _lyrics.asStateFlow()

    init {
        viewModelScope.launch {
            _newlyAddedSongCount.value = syncSongsWithDevice()
        }
    }

    fun play() {
        playerRepository.play()
    }

    fun pause() {
        playerRepository.pause()
    }

    fun skipToPrev() {
        playerRepository.skipToPreviousMedia()
    }

    fun hasPrevious() = playerRepository.hasPreviousMedia()

    fun skipToNext() {
        playerRepository.skipToNextMedia()
    }

    fun hasNext() = playerRepository.hasNextMedia()

    fun seekTo(position: Long) {
        playerRepository.seekToPosition(position)
    }

    fun fastForwardBy10Secs() {
        playerRepository.seekForward()
    }

    fun rewindBy10Secs() {
        playerRepository.seekBack()
    }

    fun toggleShuffleMode() {
        playerRepository.setShuffleModeEnabled(!shuffleModeEnabled.value)
    }

    fun toggleRepeatMode() {
        val nextRepeatMode = repeatMode.value.nextRepeatMode()
        playerRepository.setRepeatMode(nextRepeatMode)
    }

    /**
     * Parses the content of an LRC file and updates the lyrics state.
     *
     * @param song The song associated with the lyrics.
     * @param lrcContent The string content of the .lrc file.
     */
    fun addLrcFile(song: Song, lrcContent: String) {
        viewModelScope.launch {
            val parsedLyrics = parseLrc(lrcContent)
            _lyrics.value = parsedLyrics
            Logger.logError("MainVM", "Lyrics added for song: ${song.title}\n$parsedLyrics")

            updateLyrics(songId = song.id, lyrics = parsedLyrics)
        }
    }

    fun clearLyrics() {
        _lyrics.value = emptyMap()
    }

    fun updateLyricsState(song: Song?) {
        viewModelScope.launch {
            if (song == null) return@launch

            val songById = getSongsByIds(listOf(song.id))
            if (songById.isEmpty()) return@launch

            _lyrics.value = songById.first().lyrics
        }
    }

    /**
     * Parses a string containing LRC-formatted lyrics into a map of timestamps and text.
     *
     * @param lrcContent The raw string content of the LRC file.
     * @return A map where the key is the timestamp in milliseconds and the value is the lyric line.
     */
    private fun parseLrc(lrcContent: String): Map<Long, String> {
        val lyricsMap = mutableMapOf<Long, String>()
        // Regex to find timestamps like [mm:ss.xx]
        val pattern = Pattern.compile("\\[(\\d{2}):(\\d{2})\\.(\\d{2})](.*)")

        lrcContent.lines().forEach { line ->
            val matcher = pattern.matcher(line)
            if (matcher.matches()) {
                val minutes = matcher.group(1)?.toLong() ?: 0
                val seconds = matcher.group(2)?.toLong() ?: 0
                val hundredths = matcher.group(3)?.toLong() ?: 0
                val text = matcher.group(4)?.trim() ?: ""

                if (text.isNotEmpty()) {
                    val timestamp = (minutes * 60 + seconds) * 1000 + hundredths * 10
                    lyricsMap[timestamp] = text
                }
            }
        }
        return lyricsMap.toSortedMap()
    }
}
