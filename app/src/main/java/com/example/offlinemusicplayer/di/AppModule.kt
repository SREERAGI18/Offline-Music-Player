package com.example.offlinemusicplayer.di

import android.app.Application
import com.example.offlinemusicplayer.data.local.dao.PlaylistDao
import com.example.offlinemusicplayer.data.local.dao.SongsDao
import com.example.offlinemusicplayer.data.repository.MusicRepository
import com.example.offlinemusicplayer.data.repository.MusicRepositoryImpl
import com.example.offlinemusicplayer.domain.usecase.GetAllSongs
import com.example.offlinemusicplayer.domain.usecase.GetPlaylists
import com.example.offlinemusicplayer.domain.usecase.SearchSongs
import com.example.offlinemusicplayer.player.AudioFilesFetcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideRepository(app: Application, dao: PlaylistDao, audioFilesFetcher: AudioFilesFetcher): MusicRepository =
        MusicRepositoryImpl(app, dao, audioFilesFetcher)

    @Provides
    fun provideGetAllSongs(repo: MusicRepository) = GetAllSongs(repo)

    @Provides
    fun provideSearchSongs(repo: MusicRepository) = SearchSongs(repo)

    @Provides
    fun provideGetPlaylists(repo: MusicRepository) = GetPlaylists(repo)

    @Provides
    fun provideAudioFileFetcher(app: Application, songsDao: SongsDao) = AudioFilesFetcher(app, songsDao)
}