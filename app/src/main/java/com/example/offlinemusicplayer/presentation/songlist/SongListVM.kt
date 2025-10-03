package com.example.offlinemusicplayer.presentation.songlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.example.offlinemusicplayer.data.local.entity.SongsEntity
import com.example.offlinemusicplayer.domain.usecase.GetAllSongs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongListVM @Inject constructor(
    private val getAllSongs: GetAllSongs
) : ViewModel() {

    val songs: Flow<PagingData<SongsEntity>> = getAllSongs()

    fun refresh() {
        viewModelScope.launch {
//            repository.forceRefresh()
        }
    }

    override fun onCleared() {
        super.onCleared()
//        repository.cleanup()
    }
}