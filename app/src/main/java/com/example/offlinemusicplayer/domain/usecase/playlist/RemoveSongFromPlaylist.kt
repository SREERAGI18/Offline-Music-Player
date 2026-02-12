package com.example.offlinemusicplayer.domain.usecase.playlist

import com.example.offlinemusicplayer.data.repository.PlaylistRepository
import javax.inject.Inject

class RemoveSongFromPlaylist @Inject constructor(
    private val playlistRepository: PlaylistRepository
) {
    suspend operator fun invoke(songId: Long, playlistId: Long) {
        playlistRepository.removeSongFromPlaylist(songId = songId, playlistId =  playlistId)
    }
}