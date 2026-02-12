package com.example.offlinemusicplayer.domain.usecase.songs

import com.example.offlinemusicplayer.data.repository.PlaylistRepository
import com.example.offlinemusicplayer.data.repository.SongsRepository

class UpdateFavoriteSong(
    private val playlistRepository: PlaylistRepository,
    private val songsRepository: SongsRepository,
    private val getFavoriteSongs: GetFavoriteSongs
) {
    suspend operator fun invoke(songId: Long, isFav: Boolean) {
        val mostPlayedPlaylist = playlistRepository.getFavoritesPlaylist()

        if(mostPlayedPlaylist == null) return

        songsRepository.updateFavoriteSong(songId, isFav)

        val favoriteSongs = getFavoriteSongs()
        val favoriteSongIds = favoriteSongs.map { it.id }

        val updatedPlaylist = mostPlayedPlaylist.copy(
            songIds = favoriteSongIds
        )

        playlistRepository.updatePlaylist(updatedPlaylist)
    }
}