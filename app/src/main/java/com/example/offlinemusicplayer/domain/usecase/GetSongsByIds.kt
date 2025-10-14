package com.example.offlinemusicplayer.domain.usecase

import com.example.offlinemusicplayer.data.repository.SongsRepository

class GetSongsByIds(private val repo: SongsRepository) {
    suspend operator fun invoke(songIds: List<Long>) = repo.getSongsByIds(songIds)
}