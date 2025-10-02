package com.example.offlinemusicplayer.data.repository

import android.content.Context
import android.provider.MediaStore
import android.util.Log
import com.example.offlinemusicplayer.data.local.dao.PlaylistDao
import com.example.offlinemusicplayer.data.local.entity.PlaylistEntity
import com.example.offlinemusicplayer.domain.model.Playlist
import com.example.offlinemusicplayer.domain.model.Song
import com.example.offlinemusicplayer.player.AudioFilesFetcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MusicRepositoryImpl(
    private val context: Context,
    private val playlistDao: PlaylistDao,
    private val audioFilesFetcher: AudioFilesFetcher,
) : MusicRepository {

    override suspend fun getAllSongs(): List<Song> {
        Log.e("MusicRepositoryImpl", "getAllSongs")
        val songList = mutableListOf<Song>()

        val collection =
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.IS_MUSIC // Filter for actual music files
        )

        // Show only music files
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"

//        context.contentResolver.query(
//            collection,
//            projection,
//            selection,
//            null,
//            "${MediaStore.Audio.Media.TITLE} ASC" // Sort the results
//        )?.use { cursor ->
//            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
//            val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
//            val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
//            val pathCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
//            val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
//
//            while (cursor.moveToNext()) {
//                val id = cursor.getLong(idCol)
//                val title = cursor.getString(titleCol)
//                val artist = cursor.getString(artistCol)
//                val path = cursor.getString(pathCol)
//                val duration = cursor.getLong(durationCol)
//
//                Log.e("MusicRepositoryImpl", "path: $path")
//                songList.add(
//                    Song(
//                        id = id,
//                        title = title,
//                        artist = artist,
//                        path = path,
//                        duration = duration
//                    )
//                )
//            }
//        }

        songList.addAll(audioFilesFetcher.getAllAudioFiles())

        return songList
    }

    override fun getPlaylists(): Flow<List<Playlist>> =
        playlistDao.getPlaylists().map { list ->
            list.map {
                Playlist(it.id, it.name, it.songIds.split(",").mapNotNull { id -> id.toLongOrNull() })
            }
        }

    override suspend fun insertPlaylist(playlist: Playlist) {
        playlistDao.insertPlaylist(
            PlaylistEntity(
                name = playlist.name,
                songIds = playlist.songIds.joinToString(",")
            )
        )
    }
}