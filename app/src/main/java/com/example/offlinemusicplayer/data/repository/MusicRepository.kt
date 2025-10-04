package com.example.offlinemusicplayer.data.repository

import androidx.paging.PagingData
import com.example.offlinemusicplayer.data.local.entity.SongsEntity
import com.example.offlinemusicplayer.domain.model.Playlist
import kotlinx.coroutines.flow.Flow

interface MusicRepository {
    fun getAllSongsPaginated(): Flow<PagingData<SongsEntity>>
    fun searchSongsPaginated(query: String): Flow<PagingData<SongsEntity>>
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun insertPlaylist(playlist: Playlist)
    suspend fun getAllSongs(): List<SongsEntity>
}