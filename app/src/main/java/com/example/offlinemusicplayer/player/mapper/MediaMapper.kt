package com.example.offlinemusicplayer.player.mapper

import android.os.Bundle
import androidx.media3.common.MediaItem
import com.example.offlinemusicplayer.data.local.entity.SongsEntity

class MediaMapper {

    fun map(
        mediaItem: MediaItem,
        defaultArtist: String = "<Unknown>"
    ): SongsEntity {
        val metadata = mediaItem.mediaMetadata
        val extras = metadata.extras ?: Bundle.EMPTY

        return SongsEntity(
            id = mediaItem.mediaId.toLongOrNull() ?: 0,
            path = mediaItem.localConfiguration?.uri?.toString() ?: "",
            title = metadata.title?.toString() ?: "",
            artist = metadata.artist?.toString() ?: defaultArtist,
            album = metadata.albumTitle?.toString() ?: "",
            duration = extras.getLong("duration", 0L),
            size = extras.getLong("size", 0L),
            dateAdded = extras.getLong("dateAdded", 0L),
            lastScanned = System.currentTimeMillis()
        )
    }
}