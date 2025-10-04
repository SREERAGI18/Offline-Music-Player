package com.example.offlinemusicplayer.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.offlinemusicplayer.data.local.entity.SongsEntity

@Dao
interface SongsDao {

    // Paging3 requires PagingSource
    @Query("SELECT * FROM songs ORDER BY title ASC")
    fun getAllSongsPaged(): PagingSource<Int, SongsEntity>

    @Query("SELECT * FROM songs ORDER BY title ASC")
    suspend fun getAllSongs(): List<SongsEntity>

    @Query("SELECT * FROM songs WHERE title LIKE '%' || :query || '%' OR artist LIKE '%' || :query || '%' ORDER BY title ASC")
    fun searchSongsPaged(query: String): PagingSource<Int, SongsEntity>

//    @Query("SELECT * FROM songs WHERE volumeName = :volume ORDER BY title ASC")
//    fun getSongsByVolumePaged(volume: String): PagingSource<Int, SongsEntity>

    @Query("SELECT * FROM songs WHERE artist = :artist ORDER BY title ASC")
    fun getSongsByArtistPaged(artist: String): PagingSource<Int, SongsEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(audioFiles: List<SongsEntity>)

    @Query("DELETE FROM songs WHERE id NOT IN (:currentIds)")
    suspend fun deleteObsoleteFiles(currentIds: List<Long>)

    @Query("DELETE FROM songs")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM songs")
    suspend fun getCount(): Int

    @Query("SELECT MAX(lastScanned) FROM songs")
    suspend fun getLastScanTime(): Long?

    @Query("SELECT DISTINCT artist FROM songs WHERE artist IS NOT NULL ORDER BY artist ASC")
    suspend fun getAllArtists(): List<String>
}