package com.lyrisync.domain.usecase.playlist

import com.lyrisync.data.repository.PlaylistRepository
import com.lyrisync.domain.model.Playlist

class DeletePlaylist(private val repo: PlaylistRepository) {
    suspend operator fun invoke(playlist: Playlist) {
        repo.deletePlaylist(playlist)
    }
}