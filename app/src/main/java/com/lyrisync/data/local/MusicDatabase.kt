package com.lyrisync.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.lyrisync.data.local.dao.PlaylistDao
import com.lyrisync.data.local.dao.SongsDao
import com.lyrisync.data.local.entity.PlaylistEntity
import com.lyrisync.data.local.entity.SongsEntity

@Database(
    entities = [PlaylistEntity::class, SongsEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MusicDatabase : RoomDatabase() {
    abstract fun playlistDao(): PlaylistDao
    abstract fun songsDao(): SongsDao
}
