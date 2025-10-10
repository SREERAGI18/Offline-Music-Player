package com.example.offlinemusicplayer.domain.usecase

import com.example.offlinemusicplayer.data.repository.PlaylistRepository
import com.example.offlinemusicplayer.data.repository.SongsRepository

class GetPlaylists(private val repo: PlaylistRepository) {
    operator fun invoke() = repo.getPlaylists()
}