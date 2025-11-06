package com.lyrisync.player

import androidx.media3.common.MediaItem
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSessionService
import androidx.media3.session.MediaSession
import com.google.common.util.concurrent.ListenableFuture

class MediaSessionCallback: MediaLibraryService.MediaLibrarySession.Callback {
//    override fun onPlaybackResumption(
//        mediaSession: MediaSession,
//        controller: MediaSession.ControllerInfo
//    ): ListenableFuture<MediaSession.MediaItemsWithStartPosition> {
//        val lastMediaItem: MediaItem = getLastPlayedItemFromStorage()
//        val startPositionMs: Long = getSavedPlaybackPosition(lastMediaItem)
//
//        return Futures.immediateFuture(
//            MediaSession.MediaItemsWithStartPosition(
//                listOf(lastMediaItem),
//                /* startIndex = */ 0,
//                /* positionMs = */ startPositionMs
//            )
//        )
//
//    }
}