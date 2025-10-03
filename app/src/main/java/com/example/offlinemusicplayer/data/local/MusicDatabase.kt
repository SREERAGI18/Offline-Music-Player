package com.example.offlinemusicplayer.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.offlinemusicplayer.data.local.dao.PlaylistDao
import com.example.offlinemusicplayer.data.local.dao.SongsDao
import com.example.offlinemusicplayer.data.local.entity.PlaylistEntity
import com.example.offlinemusicplayer.data.local.entity.SongsEntity

@Database(
    entities = [PlaylistEntity::class, SongsEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MusicDatabase : RoomDatabase() {
    abstract fun playlistDao(): PlaylistDao
    abstract fun songsDao(): SongsDao
}
