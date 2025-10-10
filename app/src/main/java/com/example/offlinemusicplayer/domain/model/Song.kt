package com.example.offlinemusicplayer.domain.model

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Size
import androidx.core.net.toUri
import com.example.offlinemusicplayer.data.local.entity.SongsEntity

data class Song(
    val id: Long,
    val title: String,
    val artist: String?,
    val album: String?,
    val duration: Long,
    val path: String,
    val albumId: Long,
    val size: Long,
    val dateAdded: Long,
    val trackNumber: Int,
    val year: Int,
    val dateModified: Long,
    val artistId: Long,
    val composer: String?,
    val albumArtist: String?
) {
    fun toSongsEntity(): SongsEntity {
        return SongsEntity(
            id = id,
            title = title,
            artist = artist,
            album = album,
            duration = duration,
            path = path,
            albumId = albumId,
            size = size,
            dateAdded = dateAdded,
            trackNumber = trackNumber,
            year = year,
            dateModified = dateModified,
            artistId = artistId,
            composer = composer,
            albumArtist = albumArtist
        )
    }

    fun getAlbumUri(): Uri {
        val localUri = "content://media/external/audio/albumart".toUri()
        val albumUri = ContentUris.withAppendedId(localUri, albumId)
        return albumUri
    }

    fun getExistingAlbumUri(context: Context): Uri? {
        val uri = getAlbumUri()
        return try {
            // Try to open an input stream from the URI.
            // If it succeeds, the file exists. We close it immediately.
            context.contentResolver.openInputStream(uri)?.close()
            uri
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
