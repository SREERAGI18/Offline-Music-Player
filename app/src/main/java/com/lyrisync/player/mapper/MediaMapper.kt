package com.lyrisync.player.mapper

import android.os.Bundle
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.lyrisync.domain.model.Song

class MediaMapper {

    fun mapToSong(
        mediaItem: MediaItem,
        defaultArtist: String = "<Unknown>"
    ): Song {
        val metadata = mediaItem.mediaMetadata
        val extras = metadata.extras ?: Bundle.EMPTY

        return Song(
            id = mediaItem.mediaId.toLongOrNull() ?: 0,
            path = mediaItem.localConfiguration?.uri?.toString() ?: "",
            title = metadata.title?.toString() ?: "",
            artist = metadata.artist?.toString() ?: defaultArtist,
            album = metadata.albumTitle?.toString() ?: "",
            duration = extras.getLong("duration", 0L),
            size = extras.getLong("size", 0L),
            dateAdded = extras.getLong("dateAdded", 0L),
            trackNumber = extras.getInt("trackNumber", 0),
            year = extras.getInt("year", 0),
            dateModified = extras.getLong("dateModified", 0L),
            albumId = extras.getLong("albumId", 0L),
            artistId = extras.getLong("artistId", 0L),
            composer = extras.getString("composer"),
            albumArtist = extras.getString("albumArtist"),
        )
    }

    fun mapToMediaItem(
        song: Song
    ): MediaItem {
        val metadata = MediaMetadata.Builder()
            .setTitle(song.title)
            .setArtist(song.artist)
            .setAlbumTitle(song.album)
            .setExtras(Bundle().apply {
                putLong("duration", song.duration)
                putLong("size", song.size)
                putLong("dateAdded", song.dateAdded)
                putInt("trackNumber", song.trackNumber)
                putInt("year", song.year)
                putLong("dateModified", song.dateModified)
                putLong("albumId", song.albumId)
                putLong("artistId", song.artistId)
                putString("composer", song.composer)
                putString("albumArtist", song.albumArtist)
            })
            .build()

        return MediaItem.Builder()
            .setMediaId(song.id.toString())
            .setUri(song.path.toUri())
            .setMediaMetadata(metadata)
            .build()
    }
}