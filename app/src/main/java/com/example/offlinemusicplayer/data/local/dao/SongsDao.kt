package com.example.offlinemusicplayer.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.offlinemusicplayer.data.local.entity.SongsEntity

@Dao
interface SongsDao {

    @Query("SELECT * FROM songs ORDER BY title ASC")
    fun getAllSongsPaged(): PagingSource<Int, SongsEntity>

    @Query("SELECT * FROM songs ORDER BY title ASC")
    suspend fun getAllSongs(): List<SongsEntity>

    @Query("SELECT * FROM songs ORDER BY dateModified DESC, title ASC LIMIT :size")
    suspend fun getRecentSongs(size: Int): List<SongsEntity>

    @Query("SELECT * FROM songs WHERE title LIKE '%' || :query || '%' OR artist LIKE '%' || :query || '%' ORDER BY title ASC")
    fun searchSongsPaged(query: String): PagingSource<Int, SongsEntity>

    @Query("SELECT * FROM songs WHERE title LIKE '%' || :query || '%' OR artist LIKE '%' || :query || '%' ORDER BY title ASC")
    fun searchSongs(query: String): List<SongsEntity>

//    @Query("SELECT * FROM songs WHERE volumeName = :volume ORDER BY title ASC")
//    fun getSongsByVolumePaged(volume: String): PagingSource<Int, SongsEntity>

    @Query("SELECT * FROM songs WHERE id IN (:songIds)")
    fun getSongsByIdsPaginated(songIds: List<Long>): PagingSource<Int, SongsEntity>

    @Query("SELECT * FROM songs WHERE id IN (:songIds)")
    suspend fun getSongsByIds(songIds: List<Long>): List<SongsEntity>

    @Query("SELECT * FROM songs WHERE artist = :artist ORDER BY title ASC")
    fun getSongsByArtistPaged(artist: String): PagingSource<Int, SongsEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(audioFiles: List<SongsEntity>)

    @Query("DELETE FROM songs WHERE id NOT IN (:currentIds)")
    suspend fun deleteObsoleteFiles(currentIds: List<Long>)

    @Query("DELETE FROM songs")
    suspend fun deleteAll()

    @Query("DELETE FROM songs WHERE id = :songId")
    suspend fun deleteSongById(songId: Long)

    @Query("SELECT COUNT(*) FROM songs")
    suspend fun getCount(): Int

    @Query("SELECT MAX(lastScanned) FROM songs")
    suspend fun getLastScanTime(): Long?

    @Query("SELECT DISTINCT artist FROM songs WHERE artist IS NOT NULL ORDER BY artist ASC")
    suspend fun getAllArtists(): List<String>

    @Query("UPDATE songs SET playCount = playCount + 1 WHERE id = :songId")
    suspend fun incrementPlayCount(songId: Long)

    @Query("SELECT * FROM songs WHERE (playCount >= 1) ORDER BY playCount DESC LIMIT :limit")
    suspend fun getMostPlayedSongs(limit: Int): List<SongsEntity>

    @Query("SELECT id FROM songs")
    suspend fun getAllSongIds(): List<Long>

    @Query("SELECT * FROM songs WHERE isFav = :isFav")
    suspend fun getFavoriteSongs(isFav: Boolean = true): List<SongsEntity>

    @Query("UPDATE songs SET isFav = :isFav WHERE id = :songId")
    suspend fun updateFavoriteSong(songId: Long, isFav: Boolean)

    @Query("DELETE FROM songs WHERE id IN (:songIds)")
    suspend fun deleteSongsByIds(songIds: List<Long>)

    @Query("SELECT COUNT(*) FROM songs WHERE title < (SELECT title FROM songs WHERE id = :songId)")
    suspend fun getSongIndexById(songId: Long): Int

    @Query("SELECT COUNT(*) FROM songs WHERE title < (SELECT title FROM songs WHERE title LIKE :letter || '%' ORDER BY title ASC LIMIT 1)")
    suspend fun getFirstSongIndexByLetter(letter: String): Int

    @Query("UPDATE songs SET lyrics = :lyrics WHERE id = :songId")
    suspend fun updateLyrics(songId: Long, lyrics: Map<Long, String>)
}