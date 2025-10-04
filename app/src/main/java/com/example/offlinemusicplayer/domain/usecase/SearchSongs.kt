package com.example.offlinemusicplayer.domain.usecase

import com.example.offlinemusicplayer.data.repository.MusicRepository

class SearchSongs(private val repo: MusicRepository) {
    operator fun invoke(searchQuery: String) = repo.searchSongs(searchQuery)
}