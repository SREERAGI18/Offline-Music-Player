package com.lyrisync.domain.usecase.songs

import com.lyrisync.data.repository.SongsRepository

class GetSongsByIds(private val repo: SongsRepository) {
    suspend operator fun invoke(songIds: List<Long>) = repo.getSongsByIds(songIds)
}