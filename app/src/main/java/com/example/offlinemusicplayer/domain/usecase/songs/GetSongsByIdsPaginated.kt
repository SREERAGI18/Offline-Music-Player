package com.example.offlinemusicplayer.domain.usecase.songs

import com.example.offlinemusicplayer.data.repository.SongsRepository

class GetSongsByIdsPaginated(private val repo: SongsRepository) {
    operator fun invoke(songIds: List<Long>) = repo.getSongsByIdsPaginated(songIds)
}
