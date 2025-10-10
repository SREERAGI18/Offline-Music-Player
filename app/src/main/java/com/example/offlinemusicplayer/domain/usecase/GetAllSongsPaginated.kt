package com.example.offlinemusicplayer.domain.usecase

import com.example.offlinemusicplayer.data.repository.SongsRepository

class GetAllSongsPaginated(private val repo: SongsRepository) {
    operator fun invoke() = repo.getAllSongsPaginated()
}