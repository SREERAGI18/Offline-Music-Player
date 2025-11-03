package com.example.offlinemusicplayer.data.repository

import com.example.offlinemusicplayer.domain.model.Playlist
import com.example.offlinemusicplayer.domain.model.Song
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    fun getPlaylists(): Flow<List<Playlist>>
    fun getPlaylistById(playlistId: Long): Flow<Playlist?>
    suspend fun insertPlaylist(playlist: Playlist)
    suspend fun updatePlaylist(selectedSongIds: List<Long>, playlist: Playlist)
    suspend fun removeSongFromPlaylist(songId: Long, playlistId: Long)
}