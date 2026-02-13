package com.example.offlinemusicplayer.data.local.converters

import androidx.room.TypeConverter
import com.example.offlinemusicplayer.util.Logger
import kotlinx.serialization.json.Json

class LyricsConverter {
    private val tag = this.javaClass.simpleName

    @TypeConverter
    fun fromMap(value: Map<Long, String>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toMap(value: String): Map<Long, String> {
        return try {
            Json.decodeFromString(value)
        } catch (e: IllegalArgumentException) {
            Logger.logError(tag, "Error decoding lyrics map: $e")
            emptyMap()
        }
    }
}
