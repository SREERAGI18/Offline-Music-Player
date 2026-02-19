package com.example.offlinemusicplayer.data.repository

import com.example.offlinemusicplayer.data.local.dao.PlaylistDao
import com.example.offlinemusicplayer.domain.model.Playlist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao,
) : PlaylistRepository {
    override fun getPlaylists(): Flow<List<Playlist>> =
        playlistDao.getPlaylists().map { list ->
            list.map {
                it.toPlaylist()
            }
        }

    override fun getPlaylistById(playlistId: Long): Flow<Playlist?> =
        playlistDao.getPlaylistById(playlistId).map {
            it?.toPlaylist()
        }

    override suspend fun insertPlaylist(playlist: Playlist) {
        playlistDao.insertPlaylist(
            playlist.toPlaylistEntity(),
        )
    }

    override suspend fun updatePlaylist(
        selectedSongIds: List<Long>,
        playlist: Playlist,
    ) {
        playlistDao.updatePlaylist(
            playlist
                .copy(
                    songIds = selectedSongIds,
                ).toPlaylistEntity(),
        )
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistDao.updatePlaylist(playlist.toPlaylistEntity())
    }

    override suspend fun getMostPlayedPlaylist(): Playlist? {
        val mostPlayedPlaylistEntity = playlistDao.getMostPlayedPlaylist()
        return mostPlayedPlaylistEntity?.toPlaylist()
    }

    override suspend fun getFavoritesPlaylist(): Playlist? {
        val favoritesPlaylistEntity = playlistDao.getFavoritesPlaylist()
        return favoritesPlaylistEntity?.toPlaylist()
    }

    override suspend fun removeSongFromPlaylist(
        songId: Long,
        playlistId: Long,
    ) {
        // 1. Get the current playlist
        val currentPlaylist = getPlaylistById(playlistId).first()

        if (currentPlaylist != null) {
            // 2. Remove the song ID from the list
            val updatedSongIds =
                currentPlaylist.songIds.toMutableList().apply {
                    remove(songId)
                }

            // 3. update playlist with updated Ids
            updatePlaylist(updatedSongIds, currentPlaylist)
        }
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        playlistDao.deletePlaylist(playlist.toPlaylistEntity())
    }
}
