package com.lyrisync.domain.usecase.songs

import com.lyrisync.data.repository.SongsRepository
import com.lyrisync.domain.model.Song

class DeleteSongById(private val repo: SongsRepository) {
    suspend operator fun invoke(song: Song) = repo.deleteSongFileById(song)
}