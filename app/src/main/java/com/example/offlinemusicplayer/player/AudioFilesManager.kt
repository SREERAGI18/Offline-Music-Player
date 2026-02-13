package com.example.offlinemusicplayer.player

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.provider.BaseColumns
import android.provider.MediaStore
import android.provider.MediaStore.Audio.AudioColumns
import androidx.core.content.ContextCompat
import com.example.offlinemusicplayer.domain.model.Song
import com.example.offlinemusicplayer.util.Logger
import com.example.offlinemusicplayer.util.getIntFromCol
import com.example.offlinemusicplayer.util.getLongFromCol
import com.example.offlinemusicplayer.util.getStringFromCol
import com.example.offlinemusicplayer.util.getStringOrNullFromCol
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AudioFilesManager(
    private val context: Context,
) {

    /**
     * Deletes a song file from the device.
     *
     * @param song The [Song] to delete.
     * @return `true` if the file was successfully deleted, `false` otherwise.
     */
    suspend fun deleteSongFile(song: Song): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // 1. Get the content URI for the song
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    song.id
                )

                // 2. Use ContentResolver to delete the file
                val deletedRows = context.contentResolver.delete(contentUri, null, null)

                if (deletedRows > 0) {
                    // 3. If file deletion was successful, remove from the local database
                    true
                } else {
                    // Handle the case where the file couldn't be deleted
                    Logger.logError("AudioFilesFetcher", "Failed to delete file for song ID: ${song.id}")
                    false
                }
            } catch (e: SecurityException) {
                // This can happen if you don't have the correct permissions,
                // especially on Android 10+ for files you don't own.
                Logger.logError("AudioFilesFetcher", "SecurityException on deleting song: ${e.message}")
                false
            } catch (e: IllegalArgumentException) {
                // Thrown if the URI is invalid or malformed
                Logger.logError("AudioFilesFetcher", "Invalid URI for song: ${e.message}")
                false
            } catch (e: UnsupportedOperationException) {
                // Thrown if the content provider doesn't support delete
                Logger.logError("AudioFilesFetcher", "Delete not supported: ${e.message}")
                false
            }
        }
    }

    fun hasPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_MEDIA_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    // Fetch all audio files from all storage locations
    fun fetchAllSongsFromDevice(
        batchSize: Int = 100,
        onProgress: ((Int, Int) -> Unit)? = null
    ): List<Song> {
        Logger.logError("AudioFilesFetcher", "scanAndCacheSongs")

        val audioFiles = mutableListOf<Song>()

        var totalProcessed = 0
        val estimatedTotal = 0

        if (!hasPermissions()) {
            throw SecurityException("Storage permissions not granted")
        }

        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(
            BaseColumns._ID,
            AudioColumns.TITLE,
            AudioColumns.TRACK,
            AudioColumns.YEAR,
            AudioColumns.DURATION,
            AudioColumns.SIZE,
            AudioColumns.DATA,
            AudioColumns.DATE_MODIFIED,
            AudioColumns.DATE_ADDED,
            AudioColumns.ALBUM_ID,
            AudioColumns.ALBUM,
            AudioColumns.ARTIST_ID,
            AudioColumns.ARTIST,
            AudioColumns.COMPOSER,
            AudioColumns.ALBUM_ARTIST
        )

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${MediaStore.Audio.Media.DISPLAY_NAME} ASC"

        val query: Cursor? = context.contentResolver.query(
            collection,
            projection,
            selection,
            null,
            sortOrder
        )

        query?.use { cursor ->
            val batch = mutableListOf<Song>()

            while (cursor.moveToNext()) {
                val id = cursor.getLongFromCol(AudioColumns._ID)
                val title = cursor.getStringFromCol(AudioColumns.TITLE)
                val trackNumber = cursor.getIntFromCol(AudioColumns.TRACK)
                val year = cursor.getIntFromCol(AudioColumns.YEAR)
                val duration = cursor.getLongFromCol(AudioColumns.DURATION)
                val size = cursor.getLongFromCol(AudioColumns.SIZE)
                val path = cursor.getStringFromCol(MediaStore.Audio.Media.DATA)
                val dateModified = cursor.getLongFromCol(AudioColumns.DATE_MODIFIED)
                val dateAdded = cursor.getLongFromCol(AudioColumns.DATE_ADDED)
                val albumId = cursor.getLongFromCol(AudioColumns.ALBUM_ID)
                val albumName = cursor.getStringOrNullFromCol(AudioColumns.ALBUM)
                val artistId = cursor.getLongFromCol(AudioColumns.ARTIST_ID)
                val artistName = cursor.getStringOrNullFromCol(AudioColumns.ARTIST)
                val composer = cursor.getStringOrNullFromCol(AudioColumns.COMPOSER)
                val albumArtist = cursor.getStringOrNullFromCol(MediaStore.Audio.Media.ALBUM_ARTIST)

                Logger.logError(
                    "AudioFilesFetcher",
                    "Song ID: $id, Name: $title, Artist: $artistName, Album: $albumName"
                )

                val entity = Song(
                    id = id,
                    title = title,
                    artist = artistName,
                    album = albumName,
                    duration = duration,
                    size = size,
                    path = path,
                    dateAdded = dateAdded,
                    trackNumber = trackNumber,
                    year = year,
                    dateModified = dateModified,
                    albumId = albumId,
                    artistId = artistId,
                    composer = composer,
                    albumArtist = albumArtist
                )

                batch.add(entity)

                if (batch.size >= batchSize) {
                    audioFiles.addAll(batch)
                    totalProcessed += batch.size
                    onProgress?.invoke(totalProcessed, estimatedTotal)
                    batch.clear()
                }
            }

            if (batch.isNotEmpty()) {
                audioFiles.addAll(batch)
                totalProcessed += batch.size
                onProgress?.invoke(totalProcessed, estimatedTotal)
            }
        }

        return audioFiles
    }

    // Get all available storage volumes
    fun getAllStorageVolumes(): Set<String> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            return setOf(MediaStore.VOLUME_EXTERNAL)
            return MediaStore.getExternalVolumeNames(context)
        }
        return setOf()
    }

    suspend fun getMediaStoreAudioCount(): Int = withContext(Dispatchers.IO) {
        val projection = arrayOf(BaseColumns._ID)
        context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            "${MediaStore.Audio.Media.IS_MUSIC} != 0",
            null,
            null
        )?.use { it.count } ?: 0
    }
}
