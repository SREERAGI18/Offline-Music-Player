package com.example.offlinemusicplayer.domain.usecase.songs

data class SongsUseCases(
    val getAllSongs: GetAllSongs,
    val getAllSongsPaginated: GetAllSongsPaginated,
    val deleteSongById: DeleteSongById,
    val getSongsByIds: GetSongsByIds,
    val getSongsByIdsPaginated: GetSongsByIdsPaginated,
    val searchSongs: SearchSongs,
    val searchSongsPaginated: SearchSongsPaginated,
    val getRecentSongs: GetRecentSongs
)