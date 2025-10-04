package com.example.offlinemusicplayer.util

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {

    // Initialize SharedPreferences instance
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "song_preferences"
        private const val KEY_LAST_PLAYED_SONG_ID = "last_played_song_id"
        private const val KEY_LAST_PLAYED_POSITION = "last_played_position"
    }

    /**
     * Stores the last played song's ID and its playback position.
     *
     * @param songId The unique identifier of the song.
     * @param position The last playback position in milliseconds.
     */
    fun setLastPlayedSong(songId: String, position: Long) {
        sharedPreferences.edit().apply {
            putString(KEY_LAST_PLAYED_SONG_ID, songId)
            putLong(KEY_LAST_PLAYED_POSITION, position)
            apply()
        }
    }

    /**
     * Retrieves the last played song's ID from storage.
     *
     * @return The song ID, or an empty string if not found.
     */
    fun getLastPlayedSongId(): String {
        return sharedPreferences.getString(KEY_LAST_PLAYED_SONG_ID, "") ?: ""
    }

    /**
     * Retrieves the last played song's position from storage.
     *
     * @return The last playback position in milliseconds, or 0 if not found.
     */
    fun getLastPlayedPosition(): Long {
        return sharedPreferences.getLong(KEY_LAST_PLAYED_POSITION, 0L)
    }
}