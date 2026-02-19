package com.example.offlinemusicplayer.domain.enumclasses

enum class SongOptions(
    override val displayName: String,
) : Options {
    PlayNext("Play next"),
    AddToQueue("Add to queue"),
    AddToPlaylist("Add to playlist"),

//    EditSongInfo("Edit song info"),
    Delete("Delete"),
    Details("Details"),
    UpdateFavorite("Update favorite"),
}
