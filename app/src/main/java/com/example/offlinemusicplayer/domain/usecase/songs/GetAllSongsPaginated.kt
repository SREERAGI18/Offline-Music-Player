package com.example.offlinemusicplayer.domain.usecase.songs

import com.example.offlinemusicplayer.data.repository.SongsRepository

class GetAllSongsPaginated(private val repo: SongsRepository) {
    operator fun invoke() = repo.getAllSongsPaginated()
}
