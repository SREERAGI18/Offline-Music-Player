package com.example.offlinemusicplayer.data.local.converters

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json

class LyricsConverter {
    @TypeConverter
    fun fromMap(value: Map<Long, String>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toMap(value: String): Map<Long, String> {
        return try {
            Json.decodeFromString(value)
        } catch (e: Exception) {
            emptyMap()
        }
    }
}
