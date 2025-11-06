package com.lyrisync.domain.usecase.songs

import com.lyrisync.data.repository.SongsRepository

class SearchSongsPaginated(private val repo: SongsRepository) {
    operator fun invoke(searchQuery: String) = repo.searchSongsPaginated(searchQuery)
}