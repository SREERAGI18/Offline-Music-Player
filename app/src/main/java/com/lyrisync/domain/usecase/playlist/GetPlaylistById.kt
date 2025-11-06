package com.lyrisync.domain.usecase.playlist

import com.lyrisync.data.repository.PlaylistRepository

class GetPlaylistById(private val repo: PlaylistRepository) {
    operator fun invoke(playlistId: Long) = repo.getPlaylistById(playlistId)
}