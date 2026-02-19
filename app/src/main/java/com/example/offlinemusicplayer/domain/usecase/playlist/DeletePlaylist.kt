package com.example.offlinemusicplayer.domain.usecase.playlist

import com.example.offlinemusicplayer.data.repository.PlaylistRepository
import com.example.offlinemusicplayer.domain.model.Playlist

class DeletePlaylist(
    private val repo: PlaylistRepository,
) {
    suspend operator fun invoke(playlist: Playlist) {
        repo.deletePlaylist(playlist)
    }
}
