package com.lyrisync.data.repository

import androidx.paging.PagingData
import com.lyrisync.domain.model.Song
import kotlinx.coroutines.flow.Flow

interface SongsRepository {
    fun getAllSongsPaginated(): Flow<PagingData<Song>>
    fun searchSongsPaginated(query: String): Flow<PagingData<Song>>
    fun searchSongs(query: String): List<Song>
    fun getSongsByIdsPaginated(songIds: List<Long>): Flow<PagingData<Song>>
    suspend fun getSongsByIds(songIds: List<Long>): List<Song>
    fun getAllSongs(): Flow<List<Song>>
    suspend fun deleteSongFileById(song: Song)
    suspend fun recentSongs(size: Int): List<Song>
}