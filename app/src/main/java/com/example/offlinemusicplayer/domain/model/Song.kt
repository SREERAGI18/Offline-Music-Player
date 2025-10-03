package com.example.offlinemusicplayer.domain.model

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Size

data class Song(
    val id: Long,
    val title: String?,
    val artist: String?,
    val album: String?,
    val path: String,
    val duration: Long?,
    val uri: Uri?,
) {
    fun getAlbumArt(context: Context): Bitmap? {
        return if (uri != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                context.contentResolver.loadThumbnail(uri, Size(56, 56), null)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        } else {
            null
        }
    }
}
