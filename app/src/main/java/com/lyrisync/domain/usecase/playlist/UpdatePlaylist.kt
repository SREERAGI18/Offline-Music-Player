package com.lyrisync.domain.usecase.playlist

import com.lyrisync.data.repository.PlaylistRepository
import com.lyrisync.domain.model.Playlist

class UpdatePlaylist(private val repo: PlaylistRepository) {
    suspend operator fun invoke(songIds: List<Long>, playlist: Playlist,) {
        repo.updatePlaylist(songIds, playlist)
    }

    suspend operator fun invoke(playlist: Playlist,) {
        repo.updatePlaylist(playlist)
    }
}