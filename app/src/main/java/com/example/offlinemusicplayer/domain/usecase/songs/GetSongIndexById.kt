package com.example.offlinemusicplayer.domain.usecase.songs

import com.example.offlinemusicplayer.data.repository.SongsRepository

class GetSongIndexById(
    private val repo: SongsRepository,
) {
    suspend operator fun invoke(songId: Long) = repo.getSongIndexById(songId)
}
