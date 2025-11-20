package com.example.offlinemusicplayer.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.offlinemusicplayer.data.local.dao.SongsDao
import com.example.offlinemusicplayer.data.local.entity.SongsEntity
import com.example.offlinemusicplayer.domain.model.Song
import com.example.offlinemusicplayer.player.AudioFilesManager
import com.example.offlinemusicplayer.util.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SongsRepositoryImpl(
    private val songsDao: SongsDao,
    private val audioFilesManager: AudioFilesManager,
) : SongsRepository {

    override suspend fun syncSongsWithDevice(): Int {
        val songsChangeCount = getSongsChangeCount()

        if(songsChangeCount != 0) {
            songsDao.deleteAll()
            val songs  = audioFilesManager.fetchAllSongsFromDevice()
            songsDao.insertAll(songs.map { it.toSongsEntity() })
        }

        return songsChangeCount
    }

    private suspend fun getSongsChangeCount(): Int {
        val dbCount = songsDao.getCount()
        val mediaCount = audioFilesManager.getMediaStoreAudioCount()
        return mediaCount-dbCount
    }

    fun searchSongsPaged(query: String): Flow<PagingData<SongsEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { songsDao.searchSongsPaged(query) }
        ).flow
    }

    fun getSongsByArtistPaged(artist: String): Flow<PagingData<SongsEntity>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { songsDao.getSongsByArtistPaged(artist) }
        ).flow
    }

    override fun getAllSongsPaginated(): Flow<PagingData<Song>> {
        Logger.logError("MusicRepositoryImpl", "getAllSongs")

        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                prefetchDistance = Int.MAX_VALUE,
            ),
            pagingSourceFactory = { songsDao.getAllSongsPaged() }
        ).flow.map { pagingData ->
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
        if(audioFilesManager.deleteSongFile(song)) {
            songsDao.deleteSongById(song.id)
        }
    }

    override suspend fun recentSongs(size: Int): List<Song> {
        return songsDao.getRecentSongs(size = size).map {
            it.toSong()
        }
    }

    override suspend fun incrementPlayCount(songId: Long) {
        songsDao.incrementPlayCount(songId)
    }

    override suspend fun getMostPlayedSongs(limit: Int): List<Song> {
        return songsDao.getMostPlayedSongs(limit).map {
            it.toSong()
        }
    }

    override suspend fun getFirstSongIndexByLetter(letter: String) = songsDao.getFirstSongIndexByLetter(letter)

    override suspend fun getSongIndexById(songId: Long) = songsDao.getSongIndexById(songId)
}