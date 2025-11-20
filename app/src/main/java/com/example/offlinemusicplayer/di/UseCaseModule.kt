package com.example.offlinemusicplayer.di

import com.example.offlinemusicplayer.data.local.dao.PlaylistDao
import com.example.offlinemusicplayer.data.local.dao.SongsDao
import com.example.offlinemusicplayer.data.repository.PlaylistRepository
import com.example.offlinemusicplayer.data.repository.PlaylistRepositoryImpl
import com.example.offlinemusicplayer.data.repository.SongsRepository
import com.example.offlinemusicplayer.data.repository.SongsRepositoryImpl
import com.example.offlinemusicplayer.domain.usecase.playlist.CreatePlaylist
import com.example.offlinemusicplayer.domain.usecase.playlist.DeletePlaylist
import com.example.offlinemusicplayer.domain.usecase.playlist.GetPlaylistById
import com.example.offlinemusicplayer.domain.usecase.playlist.GetPlaylists
import com.example.offlinemusicplayer.domain.usecase.playlist.PlaylistUseCases
import com.example.offlinemusicplayer.domain.usecase.playlist.RemoveSongFromPlaylist
import com.example.offlinemusicplayer.domain.usecase.playlist.UpdateMostPlayedPlaylist
import com.example.offlinemusicplayer.domain.usecase.playlist.UpdatePlaylist
import com.example.offlinemusicplayer.domain.usecase.songs.DeleteSongById
import com.example.offlinemusicplayer.domain.usecase.songs.GetAllSongs
import com.example.offlinemusicplayer.domain.usecase.songs.GetAllSongsPaginated
import com.example.offlinemusicplayer.domain.usecase.songs.GetFavoriteSongs
import com.example.offlinemusicplayer.domain.usecase.songs.GetFirstSongIndexByLetter
import com.example.offlinemusicplayer.domain.usecase.songs.GetMostPlayedSongs
import com.example.offlinemusicplayer.domain.usecase.songs.GetRecentSongs
import com.example.offlinemusicplayer.domain.usecase.songs.GetSongIndexById
import com.example.offlinemusicplayer.domain.usecase.songs.GetSongsByIds
import com.example.offlinemusicplayer.domain.usecase.songs.GetSongsByIdsPaginated
import com.example.offlinemusicplayer.domain.usecase.songs.IncrementPlayCount
import com.example.offlinemusicplayer.domain.usecase.songs.SearchSongs
import com.example.offlinemusicplayer.domain.usecase.songs.SearchSongsPaginated
import com.example.offlinemusicplayer.domain.usecase.songs.SongsUseCases
import com.example.offlinemusicplayer.domain.usecase.songs.SyncSongsWithDevice
import com.example.offlinemusicplayer.domain.usecase.songs.UpdateFavoriteSong
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
        audioFilesManager: AudioFilesManager
    ): SongsRepository =
        SongsRepositoryImpl(
            songsDao = songsDao,
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
        updateMostPlayedPlaylist: UpdateMostPlayedPlaylist,
        createPlaylist: CreatePlaylist,
        getPlaylistById: GetPlaylistById,
        removeSongFromPlaylist: RemoveSongFromPlaylist,
        deletePlaylist: DeletePlaylist
    ) = PlaylistUseCases(
        getPlaylists = getPlaylists,
        updatePlaylist = updatePlaylist,
        updateMostPlayedPlaylist = updateMostPlayedPlaylist,
        createPlaylist = createPlaylist,
        getPlaylistById = getPlaylistById,
        removeSongFromPlaylist = removeSongFromPlaylist,
        deletePlaylist = deletePlaylist
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
        getRecentSongs: GetRecentSongs,
        incrementPlayCount: IncrementPlayCount,
        getMostPlayedSongs: GetMostPlayedSongs,
        getFirstSongIndexByLetter: GetFirstSongIndexByLetter,
        getSongIndexById: GetSongIndexById,
        updateFavoriteSong: UpdateFavoriteSong
    ) = SongsUseCases(
        getAllSongs = getAllSongs,
        getAllSongsPaginated = getAllSongsPaginated,
        deleteSongById = deleteSongById,
        getSongsByIds = getSongsByIds,
        getSongsByIdsPaginated = getSongsByIdsPaginated,
        searchSongs = searchSongs,
        searchSongsPaginated = searchSongsPaginated,
        getRecentSongs = getRecentSongs,
        incrementPlayCount = incrementPlayCount,
        getMostPlayedSongs = getMostPlayedSongs,
        getFirstSongIndexByLetter = getFirstSongIndexByLetter,
        getSongIndexById = getSongIndexById,
        updateFavoriteSong = updateFavoriteSong
    )

    @Provides
    fun provideGetAllSongsPaginated(repo: SongsRepository) = GetAllSongsPaginated(repo)

    @Provides
    fun provideSyncSongsWithDevice(repo: SongsRepository) = SyncSongsWithDevice(repo)

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
    fun provideGetFirstSongIndexByLetter(repo: SongsRepository) = GetFirstSongIndexByLetter(repo)

    @Provides
    fun provideGetSongIndexById(repo: SongsRepository) = GetSongIndexById(repo)

    @Provides
    fun provideGetFavoriteSongs(repo: SongsRepository) = GetFavoriteSongs(repo)

    @Provides
    fun provideUpdateFavoriteSong(
        songsRepository: SongsRepository,
        playlistRepository: PlaylistRepository,
        getFavoriteSongs: GetFavoriteSongs
    ) = UpdateFavoriteSong(
        playlistRepository = playlistRepository,
        songsRepository = songsRepository,
        getFavoriteSongs = getFavoriteSongs
    )

    @Provides
    fun provideDeleteSongById(
        songsRepository: SongsRepository,
        playlistRepository: PlaylistRepository
    ) = DeleteSongById(
        songsRepository = songsRepository,
        playlistRepository = playlistRepository
    )

    @Provides
    fun provideIncrementPlayCount(
        repo: SongsRepository,
        updateMostPlayedPlaylist: UpdateMostPlayedPlaylist
    ) = IncrementPlayCount(
        repo = repo,
        updateMostPlayedPlaylist = updateMostPlayedPlaylist
    )

    @Provides
    fun provideGetMostPlayedSongs(repo: SongsRepository) = GetMostPlayedSongs(repo)

    @Provides
    fun provideGetPlaylists(repo: PlaylistRepository) = GetPlaylists(repo)

    @Provides
    fun provideCreatePlaylist(repo: PlaylistRepository) = CreatePlaylist(repo)

    @Provides
    fun provideUpdatePlaylist(repo: PlaylistRepository) = UpdatePlaylist(repo)

    @Provides
    fun provideUpdateMostPlayedPlaylist(
        repo: PlaylistRepository,
        getMostPlayedSongs: GetMostPlayedSongs
    ) = UpdateMostPlayedPlaylist(
        playlistRepository = repo,
        getMostPlayedSongs = getMostPlayedSongs
    )

    @Provides
    fun provideGetPlaylistById(repo: PlaylistRepository) = GetPlaylistById(repo)

    @Provides
    fun provideRemoveSongFromPlaylist(repo: PlaylistRepository) = RemoveSongFromPlaylist(repo)

    @Provides
    fun provideDeletePlaylist(repo: PlaylistRepository) = DeletePlaylist(repo)
}
