package com.example.offlinemusicplayer.domain.usecase

import com.example.offlinemusicplayer.data.repository.PlaylistRepository
import com.example.offlinemusicplayer.domain.model.Playlist

class UpdatePlaylist(private val repo: PlaylistRepository) {
    suspend operator fun invoke(songIds: List<Long>, playlist: Playlist,) {
        repo.updatePlaylist(songIds, playlist)
    }
}