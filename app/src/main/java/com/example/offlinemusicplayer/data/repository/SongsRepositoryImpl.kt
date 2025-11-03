package com.example.offlinemusicplayer.data.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.offlinemusicplayer.data.local.dao.PlaylistDao
import com.example.offlinemusicplayer.data.local.dao.SongsDao
import com.example.offlinemusicplayer.domain.model.Song
import com.example.offlinemusicplayer.player.AudioFilesFetcher
import com.example.offlinemusicplayer.util.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class SongsRepositoryImpl(
    private val songsDao: SongsDao,
    private val playlistDao: PlaylistDao,
    private val audioFilesFetcher: AudioFilesFetcher,
) : SongsRepository {

    override fun getAllSongsPaginated(): Flow<PagingData<Song>> {
        Logger.logError("MusicRepositoryImpl", "getAllSongs")

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

    override suspend fun getSongsByIds(songIds: List<Long>): List<Song> {
        return songsDao.getSongsByIds(songIds).map {
            it.toSong()
        }
    }

    override fun getSongsByIdsPaginated(songIds: List<Long>): Flow<PagingData<Song>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { songsDao.getSongsByIdsPaginated(songIds) }
        ).flow.map { pagingData ->
            pagingData.map { songEntity ->
                songEntity.toSong()
            }
        }
    }

    override fun searchSongs(query: String): List<Song> {
        return songsDao.searchSongs(query).map {
            it.toSong()
        }
    }

    override fun searchSongsPaginated(query: String): Flow<PagingData<Song>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { songsDao.searchSongsPaged(query) }
        ).flow.map { pagingData ->
            pagingData.map {
                it.toSong()
            }
        }
    }

    override suspend fun deleteSongFileById(song: Song) {
        songsDao.deleteSongById(song.id)
        val allPlaylists = playlistDao.getPlaylists().first()
        for (playlistEntity in allPlaylists) {
            val playlist = playlistEntity.toPlaylist()
            if (playlist.songIds.contains(song.id)) {
                val updatedSongIds = playlist.songIds.toMutableList().apply {
                    remove(song.id)
                }
                val updatedPlaylist = playlist.copy(songIds = updatedSongIds)
                playlistDao.updatePlaylist(updatedPlaylist.toPlaylistEntity())
            }
        }
        audioFilesFetcher.deleteSongFile(song)
    }
}