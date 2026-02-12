package com.example.offlinemusicplayer.domain.usecase.songs

import com.example.offlinemusicplayer.data.repository.SongsRepository

class SearchSongsPaginated(private val repo: SongsRepository) {
    operator fun invoke(searchQuery: String) = repo.searchSongsPaginated(searchQuery)
}
