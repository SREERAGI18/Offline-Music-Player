package com.example.offlinemusicplayer.domain.usecase.songs

import com.example.offlinemusicplayer.data.repository.SongsRepository

class SearchSongs(private val repo: SongsRepository) {
    operator fun invoke(searchQuery: String) = repo.searchSongs(searchQuery)
}