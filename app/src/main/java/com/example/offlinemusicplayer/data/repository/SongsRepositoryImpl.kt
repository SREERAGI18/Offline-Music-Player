package com.example.offlinemusicplayer.data.repository

import android.util.Log
import androidx.paging.PagingData
import androidx.paging.map
import com.example.offlinemusicplayer.data.local.dao.PlaylistDao
import com.example.offlinemusicplayer.data.local.dao.SongsDao
import com.example.offlinemusicplayer.data.local.entity.PlaylistEntity
import com.example.offlinemusicplayer.data.local.entity.SongsEntity
import com.example.offlinemusicplayer.domain.model.Playlist
import com.example.offlinemusicplayer.domain.model.Song
import com.example.offlinemusicplayer.player.AudioFilesFetcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SongsRepositoryImpl(
    private val songsDao: SongsDao,
    private val audioFilesFetcher: AudioFilesFetcher,
) : SongsRepository {

    override fun getAllSongsPaginated(): Flow<PagingData<Song>> {
        Log.e("MusicRepositoryImpl", "getAllSongs")

        return audioFilesFetcher.getAllSongsPaged().map { pagingData ->
            pagingData.map {
                it.toSong()
            }
        }
    }

    override suspend fun getAllSongs(): List<Song> {
        return songsDao.getAllSongs().map {
            it.toSong()
        }
    }

    override fun searchSongsPaginated(query: String): Flow<PagingData<Song>> {
        return audioFilesFetcher.searchSongsPaged(query).map { pagingData ->
            pagingData.map {
                it.toSong()
            }
        }
    }
}