package com.example.offlinemusicplayer.domain.usecase

import com.example.offlinemusicplayer.data.repository.MusicRepository

class GetAllSongs(private val repo: MusicRepository) {
    operator fun invoke() = repo.getAllSongs()
}