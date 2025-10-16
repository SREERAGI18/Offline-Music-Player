package com.example.offlinemusicplayer.presentation.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.example.offlinemusicplayer.domain.model.Song
import com.example.offlinemusicplayer.domain.usecase.SearchSongs
import com.example.offlinemusicplayer.domain.usecase.SearchSongsPaginated
import com.example.offlinemusicplayer.player.PlayerServiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchVM @Inject constructor(
    private val searchSongsPaginated: SearchSongsPaginated,
    private val searchSongs: SearchSongs,
    private val playerRepository: PlayerServiceRepository,
): ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val songs: Flow<PagingData<Song>> = searchQuery
        .debounce(300L)
        .flatMapLatest { query ->
            searchSongsPaginated(query)
        }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setMediaList(initialSongPosition:Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val songs = searchSongs(searchQuery.value)
            Log.e("SearchVM", "songs: $songs")
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
}