package com.example.offlinemusicplayer.domain.usecase

import com.example.offlinemusicplayer.data.repository.SongsRepository

class GetAllSongs(private val repo: SongsRepository) {
    suspend operator fun invoke() = repo.getAllSongs()
}