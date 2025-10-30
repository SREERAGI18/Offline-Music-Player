package com.example.offlinemusicplayer.presentation.playlist_detail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.offlinemusicplayer.domain.model.Playlist
import com.example.offlinemusicplayer.domain.model.Song
import com.example.offlinemusicplayer.domain.usecase.GetPlaylistById
import com.example.offlinemusicplayer.domain.usecase.GetSongsByIds
import com.example.offlinemusicplayer.domain.usecase.GetSongsByIdsPaginated
import com.example.offlinemusicplayer.player.PlayerServiceRepository
import com.example.offlinemusicplayer.presentation.navigation.Screens
import com.example.offlinemusicplayer.util.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PlaylistDetailVM @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getPlaylistById: GetPlaylistById,
    private val getSongsByIdsPaginated: GetSongsByIdsPaginated,
    private val getSongsByIds: GetSongsByIds,
    private val playerRepository: PlayerServiceRepository,
): ViewModel() {
    private val playlistId: Long = savedStateHandle[Screens.PLAYLIST_ID_KEY] ?: 0L

    val playlist: StateFlow<Playlist?> = getPlaylistById(playlistId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val songs: Flow<PagingData<Song>> = playlist.flatMapLatest { currentPlaylist ->
        if (currentPlaylist != null && currentPlaylist.songIds.isNotEmpty()) {
            getSongsByIdsPaginated(currentPlaylist.songIds)
        } else {
            emptyFlow()
        }
    }.cachedIn(viewModelScope)

    fun setMediaList(initialSongPosition:Int) {
        viewModelScope.launch(Dispatchers.IO) {
            playlist.value?.let { currentPlayList ->
                val songs = getSongsByIds(currentPlayList.songIds)
                Logger.logError("PlaylistDetailVM", "songs: $songs")
                withContext(Dispatchers.Main) {
                    playerRepository.setMediaList(mediaList = songs, index = initialSongPosition)
                }
            }
        }
    }

    fun playSong(index: Int) {
        setMediaList(index)
        playerRepository.skipToMediaByIndex(index)
        playerRepository.play()
    }
}