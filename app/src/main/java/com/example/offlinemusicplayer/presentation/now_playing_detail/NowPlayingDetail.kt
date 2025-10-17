package com.example.offlinemusicplayer.presentation.now_playing_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.offlinemusicplayer.R
import com.example.offlinemusicplayer.domain.model.PlayerState
import com.example.offlinemusicplayer.domain.model.RepeatMode
import com.example.offlinemusicplayer.domain.model.Song
import com.example.offlinemusicplayer.presentation.components.CachedAlbumArt
import com.example.offlinemusicplayer.presentation.components.LyricsView
import com.example.offlinemusicplayer.presentation.home.HomeVM
import com.example.offlinemusicplayer.presentation.navigation.Screens
import com.example.offlinemusicplayer.util.toTimeMmSs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NowPlayingDetail(
    viewModel: HomeVM,
    onCollapse: () -> Unit,
    onNavigate: (Screens) -> Unit,
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
            contentScale = ContentScale.FillHeight,
            contentDescription = ""
        )
        // Add a transparent black tint over the blurred image
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f))
        )
        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetPeekHeight = if (scaffoldState.bottomSheetState.hasExpandedState) 72.dp else 10.dp,
            sheetContent = {
                PlayerControls(
                    viewModel = viewModel,
                    onNavigate = onNavigate
                )
            },
            topBar = {
                TopBar(currentSong, onCollapse)
            },
            sheetDragHandle = {
                Box(
                    modifier = Modifier
                        .width(50.dp)
                        .height(4.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.3f),
                            shape = CircleShape
                        )
                )
            },
            sheetShadowElevation = 0.dp,
            sheetContainerColor = Color.Transparent,
        ) {  paddingValues ->
            LyricsView()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlayerControls(
    viewModel: HomeVM,
    onNavigate: (Screens) -> Unit
) {

    val currentSong by viewModel.currentMedia.collectAsStateWithLifecycle()
    val currentPosition by viewModel.currentMediaPosition.collectAsStateWithLifecycle()
    val playerState by viewModel.playerState.collectAsStateWithLifecycle()
    val shuffleModeEnabled by viewModel.shuffleModeEnabled.collectAsStateWithLifecycle()
    val repeatMode by viewModel.repeatMode.collectAsStateWithLifecycle()

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

            Spacer(modifier = Modifier.width(8.dp))

            Slider(
                value = progress.toFloat(),
                valueRange = 0F..(currentSong?.duration?.toFloat() ?: 0F),
                onValueChange = {
                    progress = it.toLong()
                    viewModel.seekTo(progress)
                },
                track = { sliderState ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp) // Your desired track height
                    ) {
                        // Inactive track (the full background)
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    color = Color.White.copy(alpha = 0.3f),
                                    shape = CircleShape
                                )
                        )

                        // Active track (the progress)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(sliderState.value / (currentSong?.duration?.toFloat() ?: 1f))
                                .fillMaxHeight()
                                .background(
                                    color = Color.White,
                                    shape = CircleShape
                                )
                        )
                    }
                },
                thumb = {
                    SliderDefaults.Thumb(
                        interactionSource = remember { MutableInteractionSource() },
                        thumbSize = DpSize(18.dp, 18.dp),
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                        )
                    )
                },
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

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
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            val repeatModeIcon = when (repeatMode) {
                RepeatMode.ONE -> Icons.Filled.RepeatOne
                RepeatMode.ALL -> Icons.Filled.Repeat
                RepeatMode.OFF -> Icons.Filled.Repeat
            }
            val repeatModeIconColor = if (repeatMode == RepeatMode.OFF) {
                MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colorScheme.onPrimary
            }

            val shuffleModeIconColor = if (shuffleModeEnabled) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
            }
            PlayerIconButton(
                onClick = { viewModel.toggleRepeatMode() },
                icon = repeatModeIcon,
                contentDescription = "Current repeat mode",
                contentColor = repeatModeIconColor,
                modifier = Modifier.size(25.dp)
            )

            PlayerIconButton(
                onClick = { viewModel.rewindBy10Secs() },
                icon = Icons.Filled.Replay10,
                contentDescription = "Rewind by 10 seconds",
                modifier = Modifier.size(30.dp)
            )

            PlayerIconButton(
                onClick = { viewModel.skipToPrev() },
                icon = Icons.Filled.SkipPrevious,
                contentDescription = "Skip to previous",
                modifier = Modifier.size(40.dp)
            )

            PlayerIconButton(
                onClick = { if (isPlaying) viewModel.pause() else viewModel.play() },
                icon = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play",
                modifier = Modifier.size(60.dp)
            )

            PlayerIconButton(
                onClick = { viewModel.skipToNext() },
                icon = Icons.Filled.SkipNext,
                contentDescription = "Skip to next",
                modifier = Modifier.size(40.dp)
            )

            PlayerIconButton(
                onClick = { viewModel.fastForwardBy10Secs() },
                icon = Icons.Filled.Forward10,
                contentDescription = "Fast forward by 10 seconds",
                modifier = Modifier.size(30.dp)
            )

            PlayerIconButton(
                onClick = { viewModel.toggleShuffleMode() },
                icon = Icons.Filled.Shuffle,
                contentColor = shuffleModeIconColor,
                contentDescription = "Fast forward by 10 seconds",
                modifier = Modifier.size(25.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Absolute.Center
        ) {
            IconButton(
                onClick = {
                    onNavigate(Screens.NowPlayingQueue)
                },
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.QueueMusic,
                    contentDescription = "Show playing queue"
                )
            }
        }
    }
}

@Composable
private fun PlayerIconButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    enabled: Boolean = true
) {
    IconButton(
        onClick = onClick,
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = contentColor
        ),
        enabled = enabled,
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    song: Song?,
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
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = song?.title ?: "",
                        style = MaterialTheme.typography.titleSmall.copy(
                            textAlign = TextAlign.Start,
                            color = MaterialTheme.colorScheme.onPrimary
                        ),
                        maxLines = 1,
                        modifier = Modifier
                            .fillMaxWidth()
                            .basicMarquee()
                    )
                    Text(
                        text = song?.artist ?: "",
                        style = MaterialTheme.typography.bodySmall.copy(
                            textAlign = TextAlign.Start,
                            color = MaterialTheme.colorScheme.onPrimary
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
