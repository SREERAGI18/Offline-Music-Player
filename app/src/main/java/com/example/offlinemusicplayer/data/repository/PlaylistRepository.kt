package com.example.offlinemusicplayer.data.repository

import com.example.offlinemusicplayer.domain.model.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    fun getPlaylists(): Flow<List<Playlist>>
    fun getPlaylistById(playlistId: Long): Flow<Playlist?>
    suspend fun insertPlaylist(playlist: Playlist)
}