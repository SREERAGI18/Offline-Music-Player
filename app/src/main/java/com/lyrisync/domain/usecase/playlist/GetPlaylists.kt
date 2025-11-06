package com.lyrisync.domain.usecase.playlist

import com.lyrisync.data.repository.PlaylistRepository

class GetPlaylists(private val repo: PlaylistRepository) {
    operator fun invoke() = repo.getPlaylists()
}