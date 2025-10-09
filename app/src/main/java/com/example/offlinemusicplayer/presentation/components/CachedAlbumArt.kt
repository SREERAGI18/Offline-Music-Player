package com.example.offlinemusicplayer.presentation.components

import android.util.Size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import com.example.offlinemusicplayer.R
import com.example.offlinemusicplayer.data.local.entity.SongsEntity

@Composable
fun CachedAlbumArt(
    modifier: Modifier = Modifier,
    song: SongsEntity?,
    contentDescription:String,
    contentScale: ContentScale,
) {
    val context = LocalContext.current
    val imageUri = song?.getAlbumUri()

    val imageRequest = ImageRequest.Builder(context)
        .data(imageUri)
//        .dispatcher(Dispatchers.IO)
        .memoryCacheKey(imageUri?.path)
        .diskCacheKey(imageUri?.path)
        .diskCachePolicy(CachePolicy.ENABLED)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .build()

    AsyncImage(
        model = imageRequest,
        contentDescription = contentDescription,
        modifier = modifier,
        placeholder = painterResource(id = R.drawable.ic_music_note),
        error = painterResource(id = R.drawable.ic_music_note),
        contentScale = contentScale,
    )
}