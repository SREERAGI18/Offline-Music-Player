package com.example.offlinemusicplayer.domain.usecase.songs

import com.example.offlinemusicplayer.data.repository.SongsRepository

class GetRecentSongs(private val repo: SongsRepository) {
    suspend operator fun invoke(size: Int = 20) = repo.recentSongs(size)
}
