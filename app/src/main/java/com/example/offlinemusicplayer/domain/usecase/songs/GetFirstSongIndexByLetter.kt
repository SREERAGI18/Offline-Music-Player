package com.example.offlinemusicplayer.domain.usecase.songs

import com.example.offlinemusicplayer.data.repository.SongsRepository

class GetFirstSongIndexByLetter(private val repo: SongsRepository) {
    suspend operator fun invoke(letter: String) = repo.getFirstSongIndexByLetter(letter)
}