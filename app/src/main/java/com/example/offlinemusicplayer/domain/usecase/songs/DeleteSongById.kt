package com.example.offlinemusicplayer.domain.usecase.songs

import com.example.offlinemusicplayer.data.repository.SongsRepository
import com.example.offlinemusicplayer.domain.model.Song

class DeleteSongById(private val repo: SongsRepository) {
    suspend operator fun invoke(song: Song) = repo.deleteSongFileById(song)
}