package com.example.offlinemusicplayer.domain.model

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import com.example.offlinemusicplayer.data.local.entity.SongsEntity
import com.example.offlinemusicplayer.util.Logger
import java.io.FileNotFoundException

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
    val albumArtist: String?,
    val playCount: Int = 0,
    var selected: Boolean = false,
    var isPlaying: Boolean = false,
    var isFav: Boolean = false,
    val lyrics: Map<Long, String> = emptyMap()
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
            albumArtist = albumArtist,
            playCount = playCount,
            isFav = isFav,
            lyrics = lyrics
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
        } catch (e: FileNotFoundException) {
            Logger.logInfo("Song", "File not found: ${e.message}")
            null
        }
    }

    companion object {
        fun testSong() = Song(
            id = -1,
            title = "Suzume",
            artist = "Radwimps",
            album = "Suzume",
            duration = 0,
            path = "",
            albumId = -1,
            size = 0,
            dateAdded = 0,
            trackNumber = 0,
            year = 0,
            dateModified = 0,
            artistId = -1,
            composer = "",
            albumArtist = "",
            isPlaying = false,
            playCount = 1
        )
    }
}
