package com.example.offlinemusicplayer.data.repository

import com.example.offlinemusicplayer.domain.model.Playlist
import com.example.offlinemusicplayer.domain.model.Song
import kotlinx.coroutines.flow.Flow

interface MusicRepository {
    suspend fun getAllSongs(): List<Song>
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun insertPlaylist(playlist: Playlist)
}