package com.example.offlinemusicplayer.domain.usecase.playlist

data class PlaylistUseCases(
    val getPlaylists: GetPlaylists,
    val updatePlaylist: UpdatePlaylist,
    val updateMostPlayedPlaylist: UpdateMostPlayedPlaylist,
    val createPlaylist: CreatePlaylist,
    val getPlaylistById: GetPlaylistById,
    val removeSongFromPlaylist: RemoveSongFromPlaylist,
    val deletePlaylist: DeletePlaylist
)
