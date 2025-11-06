package com.lyrisync.di

import android.app.Application
import android.content.ComponentName
import android.content.Context
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.lyrisync.data.local.dao.PlaylistDao
import com.lyrisync.data.local.dao.SongsDao
import com.lyrisync.data.repository.PlaylistRepository
import com.lyrisync.data.repository.PlaylistRepositoryImpl
import com.lyrisync.data.repository.SongsRepository
import com.lyrisync.data.repository.SongsRepositoryImpl
import com.lyrisync.domain.usecase.playlist.CreatePlaylist
import com.lyrisync.domain.usecase.songs.DeleteSongById
import com.lyrisync.domain.usecase.songs.GetAllSongs
import com.lyrisync.domain.usecase.songs.GetAllSongsPaginated
import com.lyrisync.domain.usecase.playlist.GetPlaylistById
import com.lyrisync.domain.usecase.playlist.GetPlaylists
import com.lyrisync.domain.usecase.playlist.PlaylistUseCases
import com.lyrisync.domain.usecase.songs.GetRecentSongs
import com.lyrisync.domain.usecase.songs.GetSongsByIds
import com.lyrisync.domain.usecase.songs.GetSongsByIdsPaginated
import com.lyrisync.domain.usecase.playlist.RemoveSongFromPlaylist
import com.lyrisync.domain.usecase.songs.SearchSongs
import com.lyrisync.domain.usecase.songs.SearchSongsPaginated
import com.lyrisync.domain.usecase.playlist.UpdatePlaylist
import com.lyrisync.domain.usecase.songs.SongsUseCases
import com.lyrisync.player.AudioFilesManager
import com.lyrisync.player.MusicService
import com.lyrisync.player.PlayerServiceRepository
import com.lyrisync.player.PlayerServiceRepositoryImpl
import com.lyrisync.player.mapper.MediaMapper
import com.lyrisync.util.PreferencesManager
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