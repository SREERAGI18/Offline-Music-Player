package com.example.offlinemusicplayer.data.repository

import com.example.offlinemusicplayer.data.local.dao.PlaylistDao
import com.example.offlinemusicplayer.domain.model.Playlist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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

    override fun getPlaylistById(playlistId: Long): Flow<Playlist?> {
        return playlistDao.getPlaylistById(playlistId).map {
            it?.toPlaylist()
        }
    }

    override suspend fun insertPlaylist(playlist: Playlist) {
        playlistDao.insertPlaylist(
            playlist.toPlaylistEntity()
        )
    }

    override suspend fun removeSongFromPlaylist(songId: Long, playlistId: Long) {
        // 1. Get the current playlist
        val currentPlaylist = getPlaylistById(playlistId).first()

        if (currentPlaylist != null) {
            // 2. Remove the song ID from the list
            val updatedSongIds = currentPlaylist.songIds.toMutableList().apply {
                remove(songId)
            }

            // 3. Create an updated playlist object
            val updatedPlaylist = currentPlaylist.copy(
                songIds = updatedSongIds
            )

            // 4. Update the playlist in the database
            playlistDao.updatePlaylist(updatedPlaylist.toPlaylistEntity())
        }
    }
}