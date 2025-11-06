package com.lyrisync.domain.usecase.songs

import com.lyrisync.data.repository.SongsRepository

class GetAllSongsPaginated(private val repo: SongsRepository) {
    operator fun invoke() = repo.getAllSongsPaginated()
}