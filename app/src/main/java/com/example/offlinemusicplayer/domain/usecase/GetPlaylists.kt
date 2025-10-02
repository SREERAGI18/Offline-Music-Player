package com.example.offlinemusicplayer.domain.usecase

import com.example.offlinemusicplayer.data.repository.MusicRepository

class GetPlaylists(private val repo: MusicRepository) {
    operator fun invoke() = repo.getPlaylists()
}