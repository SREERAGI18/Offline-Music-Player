package com.example.offlinemusicplayer.di

import android.app.Application
import android.content.ComponentName
import android.content.Context
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.offlinemusicplayer.data.local.dao.PlaylistDao
import com.example.offlinemusicplayer.data.local.dao.SongsDao
import com.example.offlinemusicplayer.data.repository.PlaylistRepository
import com.example.offlinemusicplayer.data.repository.PlaylistRepositoryImpl
import com.example.offlinemusicplayer.data.repository.SongsRepository
import com.example.offlinemusicplayer.data.repository.SongsRepositoryImpl
import com.example.offlinemusicplayer.domain.usecase.playlist.CreatePlaylist
import com.example.offlinemusicplayer.domain.usecase.songs.DeleteSongById
import com.example.offlinemusicplayer.domain.usecase.songs.GetAllSongs
import com.example.offlinemusicplayer.domain.usecase.songs.GetAllSongsPaginated
import com.example.offlinemusicplayer.domain.usecase.playlist.GetPlaylistById
import com.example.offlinemusicplayer.domain.usecase.playlist.GetPlaylists
import com.example.offlinemusicplayer.domain.usecase.playlist.PlaylistUseCases
import com.example.offlinemusicplayer.domain.usecase.songs.GetRecentSongs
import com.example.offlinemusicplayer.domain.usecase.songs.GetSongsByIds
import com.example.offlinemusicplayer.domain.usecase.songs.GetSongsByIdsPaginated
import com.example.offlinemusicplayer.domain.usecase.playlist.RemoveSongFromPlaylist
import com.example.offlinemusicplayer.domain.usecase.songs.SearchSongs
import com.example.offlinemusicplayer.domain.usecase.songs.SearchSongsPaginated
import com.example.offlinemusicplayer.domain.usecase.playlist.UpdatePlaylist
import com.example.offlinemusicplayer.domain.usecase.songs.SongsUseCases
import com.example.offlinemusicplayer.player.AudioFilesManager
import com.example.offlinemusicplayer.player.MusicService
import com.example.offlinemusicplayer.player.PlayerServiceRepository
import com.example.offlinemusicplayer.player.PlayerServiceRepositoryImpl
import com.example.offlinemusicplayer.player.mapper.MediaMapper
import com.example.offlinemusicplayer.util.PreferencesManager
import com.google.common.util.concurrent.ListenableFuture
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideAudioFileFetcher(app: Application, songsDao: SongsDao) = AudioFilesManager(app, songsDao)

    @Provides
    fun providePreferenceManager(app: Application) = PreferencesManager(app)

    @Provides
    fun mediaMapper(): MediaMapper = MediaMapper()

    @Provides
    @Singleton
    fun playerRepositoryImpl(
        mediaMapper: MediaMapper,
        coroutineScope: CoroutineScope,
        preferencesManager: PreferencesManager,
        controller: Deferred<@JvmSuppressWildcards MediaController>,
    ): PlayerServiceRepository = PlayerServiceRepositoryImpl(
        mediaMapper = mediaMapper,
        coroutineScope = coroutineScope,
        preferencesManager = preferencesManager
    ).also { playerRepository ->
        coroutineScope.launch(Dispatchers.Main) {
            val player = controller.await()
            playerRepository.connect(
                player = player, onClose = player::release
            )
        }
    }

    @Provides
    fun providesCoroutineScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }

    @Provides
    @Singleton
    fun provideMediaController(
        @ApplicationContext context: Context,
        scope: CoroutineScope
    ): Deferred<MediaController> {
        return scope.async(Dispatchers.Main) {
            val sessionToken = SessionToken(
                context,
                ComponentName(
                    context,
                    MusicService::class.java
                )
            )
            val controllerFuture: ListenableFuture<MediaController> =
                MediaController.Builder(context, sessionToken).buildAsync()

            controllerFuture.await()
        }
    }
}