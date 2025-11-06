package com.lyrisync.domain.usecase.songs

import com.lyrisync.data.repository.SongsRepository

class GetSongsByIdsPaginated(private val repo: SongsRepository) {
    operator fun invoke(songIds: List<Long>) = repo.getSongsByIdsPaginated(songIds)
}