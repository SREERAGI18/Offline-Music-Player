package com.example.offlinemusicplayer.data.local.entity

import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.offlinemusicplayer.domain.model.Song

@Entity(tableName = "songs")
data class SongsEntity(
    @PrimaryKey val id: Long,
    val title: String,
    val artist: String?,
    val album: String?,
    val duration: Long,
    val size: Long,
    val path: String,
    val dateAdded: Long,
    val trackNumber: Int,
    val year: Int,
    val dateModified: Long,
    val albumId: Long,
    val artistId: Long,
    val composer: String?,
    val albumArtist: String?,
    val lastScanned: Long = System.currentTimeMillis(),
    val playCount: Int = 0,
    val isFav: Boolean = false,
    val lyrics: Map<Long, String> = emptyMap(),
) {
    fun toSong(): Song =
        Song(
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
            lyrics = lyrics,
        )

    fun getContentUri(): Uri {
        val contentUri =
            ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                id,
            )
        return contentUri
    }
}
