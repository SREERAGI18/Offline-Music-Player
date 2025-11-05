package com.example.offlinemusicplayer.domain.usecase.playlist

import com.example.offlinemusicplayer.data.repository.PlaylistRepository
import com.example.offlinemusicplayer.domain.model.Playlist

class CreatePlaylist(private val repo: PlaylistRepository) {
    suspend operator fun invoke(name: String, songIds: List<Long>) {
        repo.insertPlaylist(Playlist(name = name, songIds = songIds))
    }

    suspend operator fun invoke(id:Long, name: String, songIds: List<Long>) {
        repo.insertPlaylist(Playlist(id = id, name = name, songIds = songIds))
    }
}