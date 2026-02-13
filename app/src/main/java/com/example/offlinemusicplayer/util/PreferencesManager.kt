package com.example.offlinemusicplayer.util

import android.content.Context
import android.content.SharedPreferences
import com.example.offlinemusicplayer.domain.enumclasses.RepeatMode

class PreferencesManager(context: Context) {

    // Initialize SharedPreferences instance
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "song_preferences"
        private const val KEY_LAST_PLAYED_SONG_ID = "last_played_song_id"
        private const val KEY_LAST_PLAYED_POSITION = "last_played_position"
        private const val KEY_SHUFFLE_MODE = "shuffle_mode"
        private const val KEY_REPEAT_MODE = "repeat_mode"
    }

    /**
     * Stores the last played song's ID and its playback position.
     *
     * @param songId The unique identifier of the song.
     */
    fun setLastPlayedSong(songId: Long,) {
        sharedPreferences.edit().apply {
            putLong(KEY_LAST_PLAYED_SONG_ID, songId)
            apply()
        }
    }

    fun setLastPlayedPosition(position: Long) {
        sharedPreferences.edit().apply {
            putLong(KEY_LAST_PLAYED_POSITION, position)
            apply()
        }
    }

    /**
     * Retrieves the last played song's ID from storage.
     *
     * @return The song ID, or an empty string if not found.
     */
    fun getLastPlayedSongId(): Long {
        return sharedPreferences.getLong(KEY_LAST_PLAYED_SONG_ID, 0L)
    }

    /**
     * Retrieves the last played song's position from storage.
     *
     * @return The last playback position in milliseconds, or 0 if not found.
     */
    fun getLastPlayedPosition(): Long {
        return sharedPreferences.getLong(KEY_LAST_PLAYED_POSITION, 0L)
    }

    fun saveShuffleMode(shuffleModeEnabled: Boolean) {
        sharedPreferences.edit().apply {
            putBoolean(KEY_SHUFFLE_MODE, shuffleModeEnabled)
            apply()
        }
    }

    fun getShuffleMode(): Boolean {
        return sharedPreferences.getBoolean(KEY_SHUFFLE_MODE, false)
    }

    fun saveRepeatMode(repeatMode: RepeatMode) {
        sharedPreferences.edit().apply {
            putString(KEY_REPEAT_MODE, repeatMode.name)
            apply()
        }
    }

    fun getRepeatMode(): RepeatMode {
        val defaultString = RepeatMode.OFF.name
        return RepeatMode.valueOf(sharedPreferences.getString(KEY_REPEAT_MODE, defaultString) ?: defaultString)
    }
}
