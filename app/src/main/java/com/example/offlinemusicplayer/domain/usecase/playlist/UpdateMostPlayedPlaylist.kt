package com.example.offlinemusicplayer.domain.usecase.playlist

import com.example.offlinemusicplayer.data.repository.PlaylistRepository
import com.example.offlinemusicplayer.domain.usecase.songs.GetMostPlayedSongs

class UpdateMostPlayedPlaylist(
    private val playlistRepository: PlaylistRepository,
    private val getMostPlayedSongs: GetMostPlayedSongs,
) {
    suspend operator fun invoke() {
        val mostPlayedPlaylist = playlistRepository.getMostPlayedPlaylist()

        if (mostPlayedPlaylist == null) return

        val mostPlayedSongs = getMostPlayedSongs()
        val mostPlayedSongIds = mostPlayedSongs.map { it.id }

        val updatedPlaylist =
            mostPlayedPlaylist.copy(
                songIds = mostPlayedSongIds,
            )

        playlistRepository.updatePlaylist(updatedPlaylist)
    }
}
