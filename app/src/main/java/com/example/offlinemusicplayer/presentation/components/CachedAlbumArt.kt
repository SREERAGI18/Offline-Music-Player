package com.example.offlinemusicplayer.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import com.example.offlinemusicplayer.R
import com.example.offlinemusicplayer.domain.model.Song

@Composable
fun CachedAlbumArt(
    song: Song?,
    contentDescription: String,
    contentScale: ContentScale,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    // Key on id — stable primitive
    val imageUriString =
        remember(song?.id) {
            song?.getAlbumUri()?.toString()
        }

    val imageRequest =
        remember(imageUriString) {
            ImageRequest
                .Builder(context)
                .data(imageUriString)
                .memoryCacheKey(imageUriString)
                .diskCacheKey(imageUriString)
                .diskCachePolicy(CachePolicy.ENABLED)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .build()
        }

    AsyncImage(
        model = imageRequest,
        contentDescription = contentDescription,
        modifier = modifier,
        placeholder = painterResource(R.drawable.ic_music_note),
        error = painterResource(R.drawable.ic_music_note),
        contentScale = contentScale,
    )
}
