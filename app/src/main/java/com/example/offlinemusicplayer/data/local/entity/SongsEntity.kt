package com.example.offlinemusicplayer.data.local.entity

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import androidx.core.net.toUri
import androidx.room.Entity
import androidx.room.PrimaryKey

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
    val lastScanned: Long = System.currentTimeMillis()
) {
    fun getContentUri(): Uri {
        val contentUri = ContentUris.withAppendedId(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            id
        )
        return contentUri
    }

    fun getAlbumUri(): Uri {
        val localUri = "content://media/external/audio/albumart".toUri()
        val albumUri = ContentUris.withAppendedId(localUri, albumId)
        return albumUri
    }


    fun getAlbumArt(context: Context, size: Size = Size(56, 56)): Bitmap? {
        val uri = getContentUri()

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                context.contentResolver.loadThumbnail(uri, size, null)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        } else {
            null
        }
    }
}