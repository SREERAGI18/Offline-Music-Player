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

        try {
            songList.addAll(audioFilesFetcher.getAllAudioFiles())
        } catch (e: Exception) {
            e.printStackTrace()
        }

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