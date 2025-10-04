package com.example.offlinemusicplayer.presentation.search

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.example.offlinemusicplayer.data.local.entity.SongsEntity
import com.example.offlinemusicplayer.domain.usecase.SearchSongs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchVM @Inject constructor(
    private val searchSongs: SearchSongs
): ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val songs: Flow<PagingData<SongsEntity>> = searchQuery
        .debounce(300L)
        .flatMapLatest { query ->
            searchSongs(query)
        }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
}