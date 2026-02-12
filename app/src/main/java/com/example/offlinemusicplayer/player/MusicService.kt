package com.example.offlinemusicplayer.player

import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ServiceLifecycleDispatcher
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaLibraryService.MediaLibrarySession
import androidx.media3.session.MediaSession
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MusicService : MediaLibraryService(), LifecycleOwner {

    @Inject
    lateinit var mediaSession: MediaLibrarySession

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo) = mediaSession

    override val lifecycle: Lifecycle = ServiceLifecycleDispatcher(this).lifecycle

    override fun onDestroy() {
        super.onDestroy()
        mediaSession.release()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        mediaSession.release()
    }
}
