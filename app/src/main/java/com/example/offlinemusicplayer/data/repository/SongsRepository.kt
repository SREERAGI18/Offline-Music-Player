package com.example.offlinemusicplayer.data.repository

import androidx.paging.PagingData
import com.example.offlinemusicplayer.data.local.entity.SongsEntity
import com.example.offlinemusicplayer.domain.model.Playlist
import com.example.offlinemusicplayer.domain.model.Song
import kotlinx.coroutines.flow.Flow

interface SongsRepository {
    fun getAllSongsPaginated(): Flow<PagingData<Song>>
    fun searchSongsPaginated(query: String): Flow<PagingData<Song>>
    suspend fun getAllSongs(): List<Song>
}