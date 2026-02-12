package com.example.offlinemusicplayer.domain.usecase.playlist

import com.example.offlinemusicplayer.data.repository.PlaylistRepository

class GetPlaylists(private val repo: PlaylistRepository) {
    operator fun invoke() = repo.getPlaylists()
}
