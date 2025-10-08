package com.example.offlinemusicplayer.player.mapper

import android.os.Bundle
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.example.offlinemusicplayer.data.local.entity.SongsEntity

class MediaMapper {

    fun mapToSong(
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

    fun mapToMediaItem(
        songsEntity: SongsEntity
    ): MediaItem {
        val metadata = MediaMetadata.Builder()
            .setTitle(songsEntity.title)
            .setArtist(songsEntity.artist)
            .setAlbumTitle(songsEntity.album)
            .setExtras(Bundle().apply {
                putLong("duration", songsEntity.duration)
                putLong("size", songsEntity.size)
                putLong("dateAdded", songsEntity.dateAdded)
            })
            .build()

        return MediaItem.Builder()
            .setMediaId(songsEntity.id.toString())
            .setUri(songsEntity.path.toUri())
            .setMediaMetadata(metadata)
            .build()
    }
}