package com.example.offlinemusicplayer.data.repository

import android.content.Context
import android.util.Log
import androidx.paging.PagingData
import com.example.offlinemusicplayer.data.local.dao.PlaylistDao
import com.example.offlinemusicplayer.data.local.dao.SongsDao
import com.example.offlinemusicplayer.data.local.entity.PlaylistEntity
import com.example.offlinemusicplayer.data.local.entity.SongsEntity
import com.example.offlinemusicplayer.domain.model.Playlist
import com.example.offlinemusicplayer.player.AudioFilesFetcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MusicRepositoryImpl(
    private val context: Context,
    private val playlistDao: PlaylistDao,
    private val songsDao: SongsDao,
    private val audioFilesFetcher: AudioFilesFetcher,
) : MusicRepository {

    override fun getAllSongsPaginated(): Flow<PagingData<SongsEntity>> {
        Log.e("MusicRepositoryImpl", "getAllSongs")

        return audioFilesFetcher.getAllSongsPaged()
    }

    override suspend fun getAllSongs(): List<SongsEntity> {
        return songsDao.getAllSongs()
    }

    override fun searchSongsPaginated(query: String): Flow<PagingData<SongsEntity>> {
        return audioFilesFetcher.searchSongsPaged(query)
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