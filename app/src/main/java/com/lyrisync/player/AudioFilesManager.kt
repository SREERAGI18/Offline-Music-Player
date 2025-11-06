package com.lyrisync.player

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.database.Cursor
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.BaseColumns
import android.provider.MediaStore
import android.provider.MediaStore.Audio.AudioColumns
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.lyrisync.data.local.dao.SongsDao
import com.lyrisync.data.local.entity.SongsEntity
import com.lyrisync.domain.model.Song
import com.lyrisync.util.Logger
import com.lyrisync.util.getIntFromCol
import com.lyrisync.util.getLongFromCol
import com.lyrisync.util.getStringFromCol
import com.lyrisync.util.getStringOrNullFromCol
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs

class AudioFilesManager(
    private val context: Context,
    private val songsDao: SongsDao
) {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init {
        // Initial scan if needed
        scope.launch {
            if (needsRefresh() || songsDao.getCount() == 0) {
                scanAndCacheSongs()
            }
        }
    }

    suspend fun deleteSongFile(song: Song) {
        withContext(Dispatchers.IO) {
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
                    songsDao.deleteSongById(song.id)
                } else {
                    // Handle the case where the file couldn't be deleted
                    Logger.logError("AudioFilesFetcher", "Failed to delete file for song ID: ${song.id}")
                }
            } catch (e: SecurityException) {
                // This can happen if you don't have the correct permissions,
                // especially on Android 10+ for files you don't own.
                Logger.logError("AudioFilesFetcher", "SecurityException on deleting song: ${e.message}")
            } catch (e: Exception) {
                Logger.logError("AudioFilesFetcher", "Error deleting song: ${e.message}")
            }
        }
    }

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
    suspend fun scanAndCacheSongs(
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

//        val songsChangeCount = getSongsChangeCount()

        query?.use { cursor ->
            val batch = mutableListOf<SongsEntity>()

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

                Logger.logError("AudioFilesFetcher", "Song ID: $id, Name: $title, Artist: $artistName, Album: $albumName")

                val entity = SongsEntity(
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
                    songsDao.insertAll(batch)
                    totalProcessed += batch.size
                    onProgress?.invoke(totalProcessed, estimatedTotal)
                    batch.clear()
                }
            }

//            if(songsChangeCount > 0) {
//                Toast.makeText(context, "$songsChangeCount songs added", Toast.LENGTH_SHORT).show()
//            }

            // if last batch if less than batch size
            if (batch.isNotEmpty()) {
                songsDao.insertAll(batch)
                totalProcessed += batch.size
                onProgress?.invoke(totalProcessed, estimatedTotal)
            }
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

    fun getAllSongsPaged(): Flow<PagingData<SongsEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                prefetchDistance = 20
            ),
            pagingSourceFactory = { songsDao.getAllSongsPaged() }
        ).flow
    }

    fun searchSongsPaged(query: String): Flow<PagingData<SongsEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { songsDao.searchSongsPaged(query) }
        ).flow
    }

//    fun getSongsByVolumePaged(volume: String): Flow<PagingData<SongsEntity>> {
//        return Pager(
//            config = PagingConfig(pageSize = 20),
//            pagingSourceFactory = { songsDao.getSongsByVolumePaged(volume) }
//        ).flow
//    }

    fun getSongsByArtistPaged(artist: String): Flow<PagingData<SongsEntity>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { songsDao.getSongsByArtistPaged(artist) }
        ).flow
    }

    suspend fun forceRefresh() {
        scanAndCacheSongs()
    }

    private suspend fun needsRefresh(): Boolean {
        val dbCount = songsDao.getCount()
        val mediaCount = getMediaStoreAudioCount()
        return dbCount != mediaCount
    }

    private suspend fun getSongsChangeCount(): Int {
        val dbCount = songsDao.getCount()
        val mediaCount = getMediaStoreAudioCount()
        return mediaCount-dbCount
    }

    private suspend fun getMediaStoreAudioCount(): Int = withContext(Dispatchers.IO) {
        val projection = arrayOf(BaseColumns._ID)
        context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            "${MediaStore.Audio.Media.IS_MUSIC} != 0",
            null,
            null
        )?.use { it.count } ?: 0
    }

    fun cleanup() {
        scope.cancel()
    }
}