package com.example.offlinemusicplayer.domain.usecase

import com.example.offlinemusicplayer.data.repository.MusicRepository
import com.example.offlinemusicplayer.domain.model.Playlist

class CreatePlaylist(private val repo: MusicRepository) {
    suspend operator fun invoke(name: String, songIds: List<Long>) {
        repo.insertPlaylist(Playlist(name = name, songIds = songIds))
    }
}