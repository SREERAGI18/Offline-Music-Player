package com.example.offlinemusicplayer.data.repository

import com.example.offlinemusicplayer.data.local.dao.PlaylistDao
import com.example.offlinemusicplayer.data.local.entity.PlaylistEntity
import com.example.offlinemusicplayer.domain.model.Playlist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao,
): PlaylistRepository {
    override fun getPlaylists(): Flow<List<Playlist>> =
        playlistDao.getPlaylists().map { list ->
            list.map {
                it.toPlaylist()
            }
        }

    override suspend fun insertPlaylist(playlist: Playlist) {
        playlistDao.insertPlaylist(
            playlist.toPlaylistEntity()
        )
    }
}