package com.example.offlinemusicplayer.domain.enumclasses

enum class PlaylistOptions(override val displayName: String, val type: OptionType) : Options {
    Play(displayName = "Play", type = OptionType.Action),
    AddToQueue(displayName = "Add to queue", type = OptionType.Action),
    EditName(displayName = "Edit name", type = OptionType.Modify),
    EditContent(displayName = "Edit content", type = OptionType.Modify),
    Delete(displayName = "Delete", type = OptionType.Modify),
}
