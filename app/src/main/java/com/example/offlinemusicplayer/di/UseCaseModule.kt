package com.example.offlinemusicplayer.di

import com.example.offlinemusicplayer.data.local.dao.PlaylistDao
import com.example.offlinemusicplayer.data.local.dao.SongsDao
import com.example.offlinemusicplayer.data.repository.PlaylistRepository
import com.example.offlinemusicplayer.data.repository.PlaylistRepositoryImpl
import com.example.offlinemusicplayer.data.repository.SongsRepository
import com.example.offlinemusicplayer.data.repository.SongsRepositoryImpl
import com.example.offlinemusicplayer.domain.usecase.playlist.CreatePlaylist
import com.example.offlinemusicplayer.domain.usecase.playlist.GetPlaylistById
import com.example.offlinemusicplayer.domain.usecase.playlist.GetPlaylists
import com.example.offlinemusicplayer.domain.usecase.playlist.PlaylistUseCases
import com.example.offlinemusicplayer.domain.usecase.playlist.RemoveSongFromPlaylist
import com.example.offlinemusicplayer.domain.usecase.playlist.UpdatePlaylist
import com.example.offlinemusicplayer.domain.usecase.songs.DeleteSongById
import com.example.offlinemusicplayer.domain.usecase.songs.GetAllSongs
import com.example.offlinemusicplayer.domain.usecase.songs.GetAllSongsPaginated
import com.example.offlinemusicplayer.domain.usecase.songs.GetRecentSongs
import com.example.offlinemusicplayer.domain.usecase.songs.GetSongsByIds
import com.example.offlinemusicplayer.domain.usecase.songs.GetSongsByIdsPaginated
import com.example.offlinemusicplayer.domain.usecase.songs.SearchSongs
import com.example.offlinemusicplayer.domain.usecase.songs.SearchSongsPaginated
import com.example.offlinemusicplayer.domain.usecase.songs.SongsUseCases
import com.example.offlinemusicplayer.player.AudioFilesManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideSongsRepository(
        songsDao: SongsDao,
        getPlaylists: GetPlaylists,
        updatePlaylist: UpdatePlaylist,
        audioFilesManager: AudioFilesManager
    ): SongsRepository =
        SongsRepositoryImpl(
            songsDao = songsDao,
            getPlaylists = getPlaylists,
            updatePlaylist = updatePlaylist,
            audioFilesManager = audioFilesManager
        )

    @Provides
    fun providePlaylistRepository(
        playlistDao: PlaylistDao,
    ): PlaylistRepository = PlaylistRepositoryImpl(playlistDao = playlistDao)

    @Provides
    fun providePlaylistUsesCases(
        getPlaylists: GetPlaylists,
        updatePlaylist: UpdatePlaylist,
        createPlaylist: CreatePlaylist,
        getPlaylistById: GetPlaylistById,
        removeSongFromPlaylist: RemoveSongFromPlaylist
    ) = PlaylistUseCases(
        getPlaylists = getPlaylists,
        updatePlaylist = updatePlaylist,
        createPlaylist = createPlaylist,
        getPlaylistById = getPlaylistById,
        removeSongFromPlaylist = removeSongFromPlaylist
    )

    @Provides
    fun provideSongsUseCases(
        getAllSongs: GetAllSongs,
        getAllSongsPaginated: GetAllSongsPaginated,
        deleteSongById: DeleteSongById,
        getSongsByIds: GetSongsByIds,
        getSongsByIdsPaginated: GetSongsByIdsPaginated,
        searchSongs: SearchSongs,
        searchSongsPaginated: SearchSongsPaginated,
        getRecentSongs: GetRecentSongs
    ) = SongsUseCases(
        getAllSongs = getAllSongs,
        getAllSongsPaginated = getAllSongsPaginated,
        deleteSongById = deleteSongById,
        getSongsByIds = getSongsByIds,
        getSongsByIdsPaginated = getSongsByIdsPaginated,
        searchSongs = searchSongs,
        searchSongsPaginated = searchSongsPaginated,
        getRecentSongs = getRecentSongs
    )

    @Provides
    fun provideGetAllSongsPaginated(repo: SongsRepository) = GetAllSongsPaginated(repo)

    @Provides
    fun provideGetAllSongs(repo: SongsRepository) = GetAllSongs(repo)

    @Provides
    fun provideSearchSongsPaginated(repo: SongsRepository) = SearchSongsPaginated(repo)

    @Provides
    fun provideSearchSongs(repo: SongsRepository) = SearchSongs(repo)

    @Provides
    fun provideGetSongsByIdPaginated(repo: SongsRepository) = GetSongsByIdsPaginated(repo)

    @Provides
    fun provideGetSongsById(repo: SongsRepository) = GetSongsByIds(repo)

    @Provides
    fun provideGetRecentSongs(repo: SongsRepository) = GetRecentSongs(repo)

    @Provides
    fun provideDeleteSongById(repo: SongsRepository) = DeleteSongById(repo)

    @Provides
    fun provideGetPlaylists(repo: PlaylistRepository) = GetPlaylists(repo)

    @Provides
    fun provideCreatePlaylist(repo: PlaylistRepository) = CreatePlaylist(repo)

    @Provides
    fun provideUpdatePlaylist(repo: PlaylistRepository) = UpdatePlaylist(repo)

    @Provides
    fun provideGetPlaylistById(repo: PlaylistRepository) = GetPlaylistById(repo)

    @Provides
    fun provideRemoveSongFromPlaylist(repo: PlaylistRepository) = RemoveSongFromPlaylist(repo)
}
