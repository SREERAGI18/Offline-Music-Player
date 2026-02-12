package com.example.offlinemusicplayer.domain.usecase.songs

import com.example.offlinemusicplayer.data.repository.PlaylistRepository
import com.example.offlinemusicplayer.data.repository.SongsRepository
import com.example.offlinemusicplayer.domain.model.Song
import kotlinx.coroutines.flow.first

class DeleteSongById(
    private val songsRepository: SongsRepository,
    private val playlistRepository: PlaylistRepository,
) {
    suspend operator fun invoke(song: Song) {
        val allPlaylists = playlistRepository.getPlaylists().first()
        for (playlistEntity in allPlaylists) {
            val playlist = playlistEntity
            if (playlist.songIds.contains(song.id)) {
                val updatedSongIds = playlist.songIds.toMutableList().apply {
                    remove(song.id)
                }
                playlistRepository.updatePlaylist(updatedSongIds, playlist)
            }
        }
        songsRepository.deleteSongFileById(song)
    }
}
