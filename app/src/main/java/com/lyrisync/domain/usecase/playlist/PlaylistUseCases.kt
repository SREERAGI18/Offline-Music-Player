package com.lyrisync.domain.usecase.playlist

data class PlaylistUseCases(
    val getPlaylists: GetPlaylists,
    val updatePlaylist: UpdatePlaylist,
    val createPlaylist: CreatePlaylist,
    val getPlaylistById: GetPlaylistById,
    val removeSongFromPlaylist: RemoveSongFromPlaylist,
    val deletePlaylist: DeletePlaylist
)
