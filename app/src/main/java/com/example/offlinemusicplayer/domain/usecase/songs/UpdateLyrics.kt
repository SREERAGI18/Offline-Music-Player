package com.example.offlinemusicplayer.domain.usecase.songs

import com.example.offlinemusicplayer.data.repository.SongsRepository

class UpdateLyrics(private val repo: SongsRepository) {
    suspend operator fun invoke(songId: Long, lyrics: Map<Long, String>) = repo.updateLyrics(songId, lyrics)
}