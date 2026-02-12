package com.example.offlinemusicplayer.domain.usecase.songs

import com.example.offlinemusicplayer.data.repository.SongsRepository
import javax.inject.Inject

class SyncSongsWithDevice @Inject constructor(
    private val songsRepository: SongsRepository
) {
    suspend operator fun invoke(): Int = songsRepository.syncSongsWithDevice()
}
