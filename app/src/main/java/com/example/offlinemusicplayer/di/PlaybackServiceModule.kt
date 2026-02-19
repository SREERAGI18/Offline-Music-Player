package com.example.offlinemusicplayer.di

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import androidx.annotation.OptIn
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.ForwardingPlayer
import androidx.media3.common.Player
import androidx.media3.common.util.Clock
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.LoadControl
import androidx.media3.exoplayer.RenderersFactory
import androidx.media3.exoplayer.analytics.AnalyticsCollector
import androidx.media3.exoplayer.analytics.DefaultAnalyticsCollector
import androidx.media3.exoplayer.mediacodec.MediaCodecSelector
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.exoplayer.upstream.DefaultBandwidthMeter
import androidx.media3.extractor.DefaultExtractorsFactory
import androidx.media3.extractor.ExtractorsFactory
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.example.offlinemusicplayer.player.MediaSessionCallback
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@OptIn(UnstableApi::class)
@Module
@InstallIn(ServiceComponent::class)
object PlaybackServiceModule {
    private const val SEEK_INCREMENT_MS = 10_000L

    @Provides
    fun loadControl(): LoadControl {
        /*
        return DefaultLoadControl.Builder()
                .setBackBuffer(
             backBufferDurationMs =
             30_000,

             retainBackBufferFromKeyframe =
             false
                )
                .build()
         */
        return DefaultLoadControl
            .Builder()
            .setPrioritizeTimeOverSizeThresholds(false)
            .build()
    }

    @Provides
    fun mediaCodecSelector(): MediaCodecSelector = MediaCodecSelector.DEFAULT

    @Provides
    fun renderersFactory(service: Service): RenderersFactory = DefaultRenderersFactory(service)

    @Provides
    fun defaultAnalyticsCollector(): AnalyticsCollector =
        DefaultAnalyticsCollector(Clock.DEFAULT).apply {
//            addListener(AnalyticsEventLogger(logger))
        }

    @Provides
    fun extractorsFactory(): ExtractorsFactory = DefaultExtractorsFactory()

    @Provides
    fun mediaSourceFactory(
        @ApplicationContext context: Context,
        extractorsFactory: ExtractorsFactory,
    ): MediaSource.Factory = DefaultMediaSourceFactory(context, extractorsFactory)

    @OptIn(UnstableApi::class)
    @Provides
    fun trackSelector(service: Service): DefaultTrackSelector {
        val trackSelector = DefaultTrackSelector(service)
        trackSelector.setParameters(
            trackSelector
                .parameters
                .buildUpon(),
        )
        return trackSelector
    }

    @Provides
    fun audioAttributes(): AudioAttributes =
        AudioAttributes
            .Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build()

    @OptIn(UnstableApi::class)
    @Provides
    fun exoPlayer(
        service: Service,
        loadControl: LoadControl,
        analyticsCollector: AnalyticsCollector,
        mediaSourceFactory: MediaSource.Factory,
        renderersFactory: RenderersFactory,
        trackSelector: DefaultTrackSelector,
        audioAttributes: AudioAttributes,
    ): ExoPlayer =
        ExoPlayer
            .Builder(service)
            .setAnalyticsCollector(analyticsCollector)
            .setTrackSelector(trackSelector)
            .setMediaSourceFactory(mediaSourceFactory)
            .setAudioAttributes(audioAttributes, true)
            .setHandleAudioBecomingNoisy(true)
            .setWakeMode(C.WAKE_MODE_NETWORK)
            .setSeekForwardIncrementMs(SEEK_INCREMENT_MS)
            .setSeekBackIncrementMs(SEEK_INCREMENT_MS)
            .setRenderersFactory(renderersFactory)
            .setBandwidthMeter(DefaultBandwidthMeter.getSingletonInstance(service))
            .setLoadControl(loadControl)
            .build()
            .apply {
                addListener(analyticsCollector)
                setForegroundMode(true)
                // addListener(dataUpdates.listener)
            }

    @OptIn(UnstableApi::class)
    @Provides
    fun player(exoPlayer: ExoPlayer): Player = ForwardingPlayer(exoPlayer)

    @OptIn(UnstableApi::class)
    @Provides
    fun sessionCallback(): MediaSession.Callback =
        MediaSessionCallback(
//            browseTree = Media3BrowseTree.getInstance(context),
//            sharedPreferenceManager = sharedPreferenceManager,
//            kukuAutomotiveApiService = apiService,
//            coroutineScope = coroutineScope
        )

    @Provides
    fun mediaSession(
        service: Service,
        player: Player,
        playerNotificationIntent: PendingIntent,
        sessionCallback: MediaSession.Callback,
    ): MediaSession =
        MediaSession
            .Builder(
                service as MediaSessionService,
                player,
            ).setSessionActivity(playerNotificationIntent)
            .setCallback(sessionCallback)
            .build()
            .also {
                (service as LifecycleOwner).lifecycle.addObserver(
                    object : DefaultLifecycleObserver {
                        override fun onDestroy(owner: LifecycleOwner) {
                            it.release()
                        }
                    },
                )
            }

    @Provides
    fun mediaLibrarySession(
        service: Service,
        player: Player,
        sessionCallback: MediaSession.Callback,
    ): MediaLibraryService.MediaLibrarySession =
        MediaLibraryService.MediaLibrarySession
            .Builder(
                // service =
                service as MediaLibraryService,
                // player =
                player,
                // callback =
                sessionCallback as MediaSessionCallback,
            ).setId("com.example.offlinemusicplayer.media_session")
            .build()
}
