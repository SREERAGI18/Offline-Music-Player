package com.lyrisync.data.repository

import com.lyrisync.domain.model.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    fun getPlaylists(): Flow<List<Playlist>>
    fun getPlaylistById(playlistId: Long): Flow<Playlist?>
    suspend fun insertPlaylist(playlist: Playlist)
    suspend fun updatePlaylist(selectedSongIds: List<Long>, playlist: Playlist)
    suspend fun updatePlaylist(playlist: Playlist)
    suspend fun removeSongFromPlaylist(songId: Long, playlistId: Long)
    suspend fun deletePlaylist(playlist: Playlist)
}