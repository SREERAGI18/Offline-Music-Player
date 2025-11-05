package com.example.offlinemusicplayer.domain.usecase.playlist

import com.example.offlinemusicplayer.data.repository.PlaylistRepository

class GetPlaylistById(private val repo: PlaylistRepository) {
    operator fun invoke(playlistId: Long) = repo.getPlaylistById(playlistId)
}