package com.example.offlinemusicplayer.domain.model

data class MediaInfo(
    val samplingRate: Int,
    val bitRateInKbps: Int,
    val format:String?
)
