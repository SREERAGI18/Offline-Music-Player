package com.example.offlinemusicplayer.di

import android.app.Application
import androidx.room.Room
import com.example.offlinemusicplayer.data.local.MusicDatabase
import com.example.offlinemusicplayer.data.local.dao.PlaylistDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(app: Application): MusicDatabase =
        Room.databaseBuilder(app, MusicDatabase::class.java, "music_db")
            .fallbackToDestructiveMigration() // drop & rebuild on schema change (dev only)
            .build()

    @Provides
    fun providePlaylistDao(db: MusicDatabase): PlaylistDao = db.playlistDao()
}