package com.example.offlinemusicplayer.domain.usecase.songs

import com.example.offlinemusicplayer.data.repository.SongsRepository

class GetMostPlayedSongs(private val repo: SongsRepository) {
    suspend operator fun invoke(limit: Int = 20) = repo.getMostPlayedSongs(limit)
}