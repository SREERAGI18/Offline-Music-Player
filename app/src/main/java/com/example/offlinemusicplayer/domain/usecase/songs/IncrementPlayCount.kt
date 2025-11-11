package com.example.offlinemusicplayer.domain.usecase.songs

import com.example.offlinemusicplayer.data.repository.SongsRepository

class IncrementPlayCount(private val repo: SongsRepository) {
    suspend operator fun invoke(songId: Long) = repo.incrementPlayCount(songId)
}