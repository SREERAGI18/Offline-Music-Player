package com.example.offlinemusicplayer.domain.enum_classes

enum class PlayerState {

    /**
     * The player is idle, meaning it holds only limited resources. The player must be prepared
     * before it will play the media.
     */
    Idle,

    /**
     * The player is not able to immediately play the media, but is doing work toward being able to
     * do so. This state typically occurs when the player needs to buffer more data before playback
     * can start.
     */
    Loading,

    /**
     * The player is able to immediately play from its current position.
     */
    Ready,

    /**
     * The player is playing.
     */
    Playing,

    /**
     * The player has finished playing the media.
     */
    Ended
}