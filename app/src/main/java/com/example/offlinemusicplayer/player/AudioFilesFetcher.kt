package com.example.offlinemusicplayer.player

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import com.example.offlinemusicplayer.domain.model.Song

class AudioFilesFetcher(private val context: Context) {

    // Check if permissions are granted
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
    fun getAllAudioFiles(): List<Song> {
        val audioFiles = mutableListOf<Song>()

        if (!hasPermissions()) {
            throw SecurityException("Storage permissions not granted")
        }

        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DATE_ADDED
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
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val artist = cursor.getString(artistColumn)
                val album = cursor.getString(albumColumn)
                val duration = cursor.getLong(durationColumn)
                val size = cursor.getLong(sizeColumn)
                val path = cursor.getString(dataColumn)
                val dateAdded = cursor.getLong(dateColumn)

                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                audioFiles.add(
                    Song(
                        id = id,
                        title = name,
                        artist = artist,
//                        album = album,
                        duration = duration,
//                        size = size,
                        path = path,
//                        uri = contentUri,
//                        dateAdded = dateAdded
                    )
                )
            }
        }

        return audioFiles
    }

    // Fetch audio files from specific volume (for SD card support)
    fun getAudioFilesFromVolume(volumeName: String): List<Song> {
        val audioFiles = mutableListOf<Song>()

        if (!hasPermissions()) {
            throw SecurityException("Storage permissions not granted")
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return getAllAudioFiles()
        }

        val collection = MediaStore.Audio.Media.getContentUri(volumeName)

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DATE_ADDED
        )

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${MediaStore.Audio.Media.DISPLAY_NAME} ASC"

        try {
            val query: Cursor? = context.contentResolver.query(
                collection,
                projection,
                selection,
                null,
                sortOrder
            )

            query?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
                val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
                val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val name = cursor.getString(nameColumn)
                    val artist = cursor.getString(artistColumn)
                    val album = cursor.getString(albumColumn)
                    val duration = cursor.getLong(durationColumn)
                    val size = cursor.getLong(sizeColumn)
                    val path = cursor.getString(dataColumn)
                    val dateAdded = cursor.getLong(dateColumn)

                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.getContentUri(volumeName),
                        id
                    )

                    audioFiles.add(
                        Song(
                            id = id,
                            title = name,
                            artist = artist,
//                            album = album,
                            duration = duration,
//                            size = size,
                            path = path,
//                            uri = contentUri,
//                            dateAdded = dateAdded
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return audioFiles
    }

    // Get all available storage volumes
    fun getAllStorageVolumes(): Set<String> {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return setOf(MediaStore.VOLUME_EXTERNAL)
        }
        return MediaStore.getExternalVolumeNames(context)
    }

    // Fetch audio files from all available volumes
    fun getAllAudioFilesFromAllVolumes(): List<Song> {
        val allAudioFiles = mutableListOf<Song>()
        val volumes = getAllStorageVolumes()

        for (volume in volumes) {
            allAudioFiles.addAll(getAudioFilesFromVolume(volume))
        }

        return allAudioFiles
    }
}