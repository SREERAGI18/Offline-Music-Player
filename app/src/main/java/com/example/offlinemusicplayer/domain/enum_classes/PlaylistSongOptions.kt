package com.example.offlinemusicplayer.domain.enum_classes

enum class PlaylistSongOptions(override val displayName: String, val type: OptionType): Options {
    PlayNext(displayName = "Play next", type = OptionType.Action),
    AddToQueue(displayName = "Add to queue", type = OptionType.Modify),
    RemoveFromPlaylist(displayName = "Remove from playlist", type = OptionType.Modify),
}