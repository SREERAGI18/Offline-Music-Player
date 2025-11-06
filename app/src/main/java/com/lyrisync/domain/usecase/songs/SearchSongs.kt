package com.lyrisync.domain.usecase.songs

import com.lyrisync.data.repository.SongsRepository

class SearchSongs(private val repo: SongsRepository) {
    operator fun invoke(searchQuery: String) = repo.searchSongs(searchQuery)
}