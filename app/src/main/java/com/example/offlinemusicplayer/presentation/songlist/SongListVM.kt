package com.example.offlinemusicplayer.presentation.songlist

import android.app.RecoverableSecurityException
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.IntentSenderRequest
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.offlinemusicplayer.MainActivity
import com.example.offlinemusicplayer.data.local.entity.PlaylistEntity
import com.example.offlinemusicplayer.domain.model.Playlist
import com.example.offlinemusicplayer.domain.model.Song
import com.example.offlinemusicplayer.domain.usecase.playlist.PlaylistUseCases
import com.example.offlinemusicplayer.domain.usecase.songs.SongsUseCases
import com.example.offlinemusicplayer.player.PlayerServiceRepository
import com.example.offlinemusicplayer.util.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SongListVM @Inject constructor(
    private val playlistUseCases: PlaylistUseCases,
    private val songsUseCases: SongsUseCases,
    private val playerRepository: PlayerServiceRepository,
) : ViewModel() {

    val songs = mutableStateListOf<Song>()
    val currentMedia = playerRepository.currentMedia
    var contentUriToDelete: Uri? = null

    val playlists = mutableStateListOf<Playlist>()

    private val _deleteProgress: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val deleteProgress = _deleteProgress.asStateFlow()

    private val _intentSenderRequest: MutableStateFlow<IntentSenderRequest?> = MutableStateFlow(null)
    val intentSenderRequest = _intentSenderRequest.asStateFlow()

    init {
        setMediaList(0)
        viewModelScope.launch {
            songs.addAll(songsUseCases.getAllSongs())
        }
        getPlaylist()
    }

    fun setMediaList(initialSongPosition:Int) {
        viewModelScope.launch(Dispatchers.IO) {
            playerRepository.connected.collectLatest {
                if(!it) return@collectLatest

                val songs = songsUseCases.getAllSongs()
                Logger.logError("SongListVM", "songs: $songs")
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

    fun getMediaIndex(currentMedia: Song?): Int {
        val index = songs.indexOfFirst { it.id == currentMedia?.id }

        return index
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

    fun checkIfSongCanBeDeleted(song: Song, context: Context) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentUri = ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                song.id
            )
            contentUriToDelete = contentUri
            try {
                context.contentResolver.delete(contentUri, null, null)
            } catch (e: SecurityException) {
                // We caught the exception! Return the IntentSender to the caller.
                if(e is RecoverableSecurityException) {
                    _intentSenderRequest.value = IntentSenderRequest
                        .Builder(e.userAction.actionIntent.intentSender)
                        .build()
                }
            } catch (e: Exception) {

            }
        } else {
            (context as? MainActivity)?.apply {
                if(!checkIfWriteAccessGranted()) {
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
                if(it.isEmpty()) createDefaultPlaylists()

                playlists.clear()
                playlists.addAll(it)
                Log.d("SongListVM", "playlists: $playlists")
            }
        }
    }

    fun addToPlaylist(song: Song, playlist: Playlist) {
        viewModelScope.launch {
            val updatedSongIds = playlist.songIds + song.id
            playlistUseCases.updatePlaylist(songIds = updatedSongIds, playlist = playlist)
        }
    }

    fun createDefaultPlaylists() {
        viewModelScope.launch {
            PlaylistEntity.DEFAULT_PLAYLIST_MAP.map {
                playlistUseCases.createPlaylist(
                    id = it.key,
                    name = it.value,
                    songIds = emptyList()
                )
            }
        }
    }


    fun resetIntentSenderRequest() {
        _intentSenderRequest.value = null
    }

}