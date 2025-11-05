package com.example.offlinemusicplayer.data.repository

import androidx.paging.PagingData
import com.example.offlinemusicplayer.domain.model.Song
import kotlinx.coroutines.flow.Flow

interface SongsRepository {
    fun getAllSongsPaginated(): Flow<PagingData<Song>>
    fun searchSongsPaginated(query: String): Flow<PagingData<Song>>
    fun searchSongs(query: String): List<Song>
    fun getSongsByIdsPaginated(songIds: List<Long>): Flow<PagingData<Song>>
    suspend fun getSongsByIds(songIds: List<Long>): List<Song>
    suspend fun getAllSongs(): List<Song>
    suspend fun deleteSongFileById(song: Song)
    suspend fun recentSongs(size: Int): List<Song>
}