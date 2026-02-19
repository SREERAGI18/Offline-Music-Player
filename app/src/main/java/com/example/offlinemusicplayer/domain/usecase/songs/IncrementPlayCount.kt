package com.example.offlinemusicplayer.domain.usecase.songs

import com.example.offlinemusicplayer.data.repository.SongsRepository
import com.example.offlinemusicplayer.domain.usecase.playlist.UpdateMostPlayedPlaylist

class IncrementPlayCount(
    private val repo: SongsRepository,
    private val updateMostPlayedPlaylist: UpdateMostPlayedPlaylist,
) {
    suspend operator fun invoke(songId: Long) {
        repo.incrementPlayCount(songId)
        updateMostPlayedPlaylist()
    }
}
