package com.example.offlinemusicplayer.presentation.search

import android.app.RecoverableSecurityException
import android.content.ContentUris
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.IntentSenderRequest
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.example.offlinemusicplayer.MainActivity
import com.example.offlinemusicplayer.domain.model.Playlist
import com.example.offlinemusicplayer.domain.model.Song
import com.example.offlinemusicplayer.domain.usecase.playlist.PlaylistUseCases
import com.example.offlinemusicplayer.domain.usecase.songs.SongsUseCases
import com.example.offlinemusicplayer.player.PlayerServiceRepository
import com.example.offlinemusicplayer.util.Constants.DEBOUNCE_IN_MS
import com.example.offlinemusicplayer.util.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchVM
    @Inject
    constructor(
        private val playlistUseCases: PlaylistUseCases,
        private val songsUseCases: SongsUseCases,
        private val playerRepository: PlayerServiceRepository,
    ) : ViewModel() {
        private val _searchQuery = MutableStateFlow("")
        val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

        private val _deleteProgress: MutableStateFlow<Boolean> = MutableStateFlow(false)
        val deleteProgress = _deleteProgress.asStateFlow()

        private val _intentSenderRequest: MutableStateFlow<IntentSenderRequest?> = MutableStateFlow(null)
        val intentSenderRequest = _intentSenderRequest.asStateFlow()

        val playlists = mutableStateListOf<Playlist>()

        val songs: Flow<PagingData<Song>> =
            searchQuery
                .debounce(DEBOUNCE_IN_MS)
                .flatMapLatest { query ->
                    songsUseCases.searchSongsPaginated(query)
                }

        init {
            getPlaylist()
        }

        fun updateSearchQuery(query: String) {
            _searchQuery.value = query
        }

        fun setMediaList(mediaIndex: Int) {
            viewModelScope.launch(Dispatchers.IO) {
                val songs = songsUseCases.searchSongs(searchQuery.value)
                Logger.logError("SearchVM", "songs: $songs")
                withContext(Dispatchers.Main) {
                    playerRepository.setMediaList(mediaList = songs, index = mediaIndex)
                }
            }
        }

        fun playSong(index: Int) {
            setMediaList(index)
            playerRepository.skipToMediaByIndex(index)
            playerRepository.play()
        }

        fun playNext(song: Song) {
            viewModelScope.launch {
                // Find if the song already exists in the playlist
                val existingIndex = playerRepository.findIndexOfSongInPlaylist(song.id)
                val currentIndex = playerRepository.getCurrentMediaIndex()
                val nextIndex = currentIndex + 1

                if (existingIndex != null) {
                    // Song exists, move it
                    playerRepository.moveMedia(existingIndex, nextIndex)
                } else {
                    // Song doesn't exist, add it
                    playerRepository.addMedia(nextIndex, song)
                }
            }
        }

        fun addToQueue(song: Song) {
            viewModelScope.launch {
                val existingIndex = playerRepository.findIndexOfSongInPlaylist(song.id)
                if (existingIndex == null) {
                    // Only add the song if it's not already in the queue
                    playerRepository.addMedia(song)
                }
            }
        }

        fun deleteSongFile(song: Song) {
            viewModelScope.launch {
                _deleteProgress.value = true
                songsUseCases.deleteSongById(song)
                _deleteProgress.value = false
            }
        }

        fun checkIfSongCanBeDeleted(
            song: Song,
            context: Context,
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentUri =
                    ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        song.id,
                    )

                try {
                    context.contentResolver.delete(contentUri, null, null)
                } catch (e: RecoverableSecurityException) {
                    _intentSenderRequest.value =
                        IntentSenderRequest
                            .Builder(e.userAction.actionIntent.intentSender)
                            .build()
                } catch (e: SecurityException) {
                    Logger.logError("SearchVM", "SecurityException: ${e.message}")
                }
            } else {
                (context as? MainActivity)?.apply {
                    if (!checkIfWriteAccessGranted()) {
                        requestStoragePermission()
                    } else {
                        deleteSongFile(song)
                    }
                }
            }
        }

        fun getPlaylist() {
            viewModelScope.launch {
                playlistUseCases.getPlaylists().collectLatest {
                    playlists.clear()
                    playlists.addAll(it)
                    Log.d("SongListVM", "playlists: $playlists")
                }
            }
        }

        fun addToPlaylist(
            song: Song,
            playlist: Playlist,
        ) {
            viewModelScope.launch {
                val updatedSongIds = playlist.songIds + song.id
                playlistUseCases.updatePlaylist(songIds = updatedSongIds, playlist = playlist)
            }
        }

        fun updateFavorite(song: Song) {
            viewModelScope.launch {
                songsUseCases.updateFavoriteSong(songId = song.id, isFav = !song.isFav)
            }
        }

        fun resetIntentSenderRequest() {
            _intentSenderRequest.value = null
        }
    }
