package com.example.offlinemusicplayer.domain.usecase

import com.example.offlinemusicplayer.data.repository.MusicRepository

class GetAllSongsPaginated(private val repo: MusicRepository) {
    operator fun invoke() = repo.getAllSongsPaginated()
}