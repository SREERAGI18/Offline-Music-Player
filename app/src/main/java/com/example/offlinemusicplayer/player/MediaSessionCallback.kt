package com.example.offlinemusicplayer.player

import androidx.media3.session.MediaLibraryService

class MediaSessionCallback : MediaLibraryService.MediaLibrarySession.Callback {
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
