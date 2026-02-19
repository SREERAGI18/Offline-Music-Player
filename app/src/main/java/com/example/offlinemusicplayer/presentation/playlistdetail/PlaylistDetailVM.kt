package com.example.offlinemusicplayer.presentation.playlistdetail

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.offlinemusicplayer.domain.model.Playlist
import com.example.offlinemusicplayer.domain.model.Song
import com.example.offlinemusicplayer.domain.usecase.playlist.PlaylistUseCases
import com.example.offlinemusicplayer.domain.usecase.songs.SongsUseCases
import com.example.offlinemusicplayer.player.PlayerServiceRepository
import com.example.offlinemusicplayer.presentation.navigation.Screens
import com.example.offlinemusicplayer.util.Constants.TIMEOUT_IN_MS
import com.example.offlinemusicplayer.util.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PlaylistDetailVM
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        private val playlistUseCases: PlaylistUseCases,
        private val songsUseCases: SongsUseCases,
        private val playerRepository: PlayerServiceRepository,
    ) : ViewModel() {
        private val playlistId: Long = savedStateHandle[Screens.PLAYLIST_ID_KEY] ?: 0L

        val currentMedia = playerRepository.currentMedia

        val playlist: StateFlow<Playlist?> =
            playlistUseCases
                .getPlaylistById(playlistId)
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(TIMEOUT_IN_MS),
                    initialValue = null,
                )

        val songs = mutableStateListOf<Song>()

        init {
            getSongs()
        }

        fun getSongs() {
            viewModelScope.launch {
                playlist.collectLatest { currentPlayList ->
                    if (currentPlayList != null) {
                        songs.clear()
                        songs.addAll(songsUseCases.getSongsByIds(currentPlayList.songIds))
                    }
                }
            }
        }

        fun setMediaList(initialSongPosition: Int) {
            viewModelScope.launch(Dispatchers.IO) {
                Logger.logError("PlaylistDetailVM", "songs: $songs")
                withContext(Dispatchers.Main) {
                    playerRepository.setMediaList(mediaList = songs, index = initialSongPosition)
                }
            }
        }

        fun playSong(index: Int) {
            setMediaList(index)
            playerRepository.skipToMediaByIndex(index)
            playerRepository.play()
        }

        fun removeSongFromPlaylist(song: Song) {
            viewModelScope.launch {
                playlistUseCases.removeSongFromPlaylist(playlistId = playlistId, songId = song.id)
                songs.remove(song)
            }
        }

        fun moveSong(
            from: Int,
            to: Int,
        ) {
            songs.add(to, songs.removeAt(from))
            playerRepository.moveMedia(from, to)
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

        fun updatePlaylistName(playlistName: String) {
            viewModelScope.launch(Dispatchers.IO) {
                val playlist = playlist.value ?: return@launch

                playlistUseCases.updatePlaylist(
                    playlist.copy(name = playlistName),
                )
            }
        }

        fun updatePlaylistContent(songs: List<Song>) {
            viewModelScope.launch(Dispatchers.IO) {
                val playlist = playlist.value ?: return@launch

                val songIds = songs.map { it.id }
                playlistUseCases.updatePlaylist(
                    playlist.copy(songIds = songIds),
                )
            }
        }

        fun deletePlaylist() {
            viewModelScope.launch(Dispatchers.IO) {
                val playlist = playlist.value ?: return@launch

                playlistUseCases.deletePlaylist(playlist)
            }
        }

        fun addAllSongsToQueue(context: Context) {
            viewModelScope.launch(Dispatchers.IO) {
                val playlist = playlist.value ?: return@launch

                val songs = songsUseCases.getSongsByIds(playlist.songIds)
                playerRepository.addMedia(songs)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "${songs.size} Songs added to queue", Toast.LENGTH_SHORT).show()
                }
            }
        }

        fun playAllSongsOfPlaylist() {
            viewModelScope.launch(Dispatchers.IO) {
                val playlist = playlist.value ?: return@launch

                val songs = songsUseCases.getSongsByIds(playlist.songIds)
                playerRepository.setMediaList(songs)
                playerRepository.play()
            }
        }
    }
