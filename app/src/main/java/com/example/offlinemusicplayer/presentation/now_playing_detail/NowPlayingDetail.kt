package com.example.offlinemusicplayer.presentation.now_playing_detail

import android.util.Size
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.offlinemusicplayer.R
import com.example.offlinemusicplayer.data.local.entity.SongsEntity
import com.example.offlinemusicplayer.domain.model.PlayerState
import com.example.offlinemusicplayer.presentation.components.CachedAlbumArt
import com.example.offlinemusicplayer.presentation.components.LyricsView
import com.example.offlinemusicplayer.presentation.main.MainVM
import com.example.offlinemusicplayer.util.toTimeMmSs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NowPlayingDetail(
    viewModel: MainVM,
    onCollapse: () -> Unit
) {

    val currentSong by viewModel.currentMedia.collectAsStateWithLifecycle()

    val sheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.Expanded,
        skipHiddenState = true
    )
    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = sheetState)

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        CachedAlbumArt(
            song = currentSong,
            modifier = Modifier
                .fillMaxSize()
                .blur(radius = 24.dp),
            contentScale = ContentScale.Crop,
            contentDescription = ""
        )
        // Add a transparent black tint over the blurred image
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
        )
        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetPeekHeight = if (currentSong != null) 72.dp else 20.dp,
            sheetContent = {
                PlayerControls(viewModel)
            },
            topBar = {
                TopBar(currentSong, onCollapse)
            },
            sheetContainerColor = Color.Transparent,
        ) {  paddingValues ->
            LyricsView()
        }
    }
}

@Composable
private fun PlayerControls(
    viewModel: MainVM
) {

    val currentSong by viewModel.currentMedia.collectAsStateWithLifecycle()
    val currentPosition by viewModel.currentMediaPosition.collectAsStateWithLifecycle()
    val playerState by viewModel.playerState.collectAsStateWithLifecycle()

    val isPlaying = playerState == PlayerState.Playing

    var progress by remember { mutableLongStateOf(0L) }

    LaunchedEffect(currentPosition) {
        progress = currentPosition ?: 0L
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = currentPosition.toTimeMmSs(),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.width(40.dp)
            )

            Slider(
                valueRange = 0F..(currentSong?.duration?.toFloat() ?: 0F),
                value = progress.toFloat(),
                onValueChange = {},
                onValueChangeFinished = {
                    viewModel.seekTo(progress)
                },
                modifier = Modifier.weight(1f)
            )

            Text(
                text = currentSong?.duration.toTimeMmSs(),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.width(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = {
                    viewModel.rewindBy10Secs()
                },
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                modifier = Modifier.size(size = 50.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Replay10,
                    contentDescription = "Rewind by 10 seconds",
                )
            }

            IconButton(
                onClick = {
                    viewModel.skipToPrev()
                },
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                modifier = Modifier.size(size = 50.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.SkipPrevious,
                    contentDescription = "Skip to previous",
                )
            }

            IconButton(
                onClick = {
                    if (isPlaying) {
                        viewModel.pause()
                    } else {
                        viewModel.play()
                    }
                },
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                modifier = Modifier.size(size = 50.dp),
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                )
            }

            IconButton(
                onClick = {
                    viewModel.skipToNext()
                },
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                modifier = Modifier.size(size = 50.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.SkipNext,
                    contentDescription = "Skip to next",
                )
            }

            IconButton(
                onClick = {
                    viewModel.fastForwardBy10Secs()
                },
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                modifier = Modifier.size(size = 50.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Forward10,
                    contentDescription = "Fast forward by 10 seconds",
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    song: SongsEntity?,
    onCollapse: () -> Unit
) {
    TopAppBar(
        navigationIcon = {
            IconButton(
                onClick = onCollapse
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AsyncImage(
                    model = song?.getAlbumUri(),
                    contentDescription = "Album art for ${song?.title}",
                    placeholder = painterResource(id = R.drawable.ic_music_note),
                    error = painterResource(id = R.drawable.ic_music_note),
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(4.dp)),
                )
                Spacer(modifier = Modifier.width(4.dp))
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = song?.title ?: "",
                        style = MaterialTheme.typography.titleSmall.copy(
                            textAlign = TextAlign.Start
                        ),
                        maxLines = 1,
                        modifier = Modifier
                            .fillMaxWidth()
                            .basicMarquee()
                    )
                    Text(
                        text = song?.artist ?: "",
                        style = MaterialTheme.typography.bodySmall.copy(
                            textAlign = TextAlign.Start
                        ),
                        maxLines = 1,
                        modifier = Modifier
                            .fillMaxWidth()
                            .basicMarquee()
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
        )
    )
}
