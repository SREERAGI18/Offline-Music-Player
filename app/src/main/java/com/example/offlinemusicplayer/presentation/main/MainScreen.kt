package com.example.offlinemusicplayer.presentation.main

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.MediaItem
import com.example.offlinemusicplayer.player.rememberMusicController
import com.example.offlinemusicplayer.presentation.playlist.PlaylistScreen
import com.example.offlinemusicplayer.presentation.songlist.SongListScreen
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(

) {
    val context = LocalContext.current
    // A list to hold the titles of the tabs
    val tabs = remember { listOf("Songs", "Playlists") }
    // A PagerState to control the selected tab and swipe behavior
    val pagerState = rememberPagerState { tabs.size }
    // A CoroutineScope to handle tab clicks
    val scope = rememberCoroutineScope()
    val controller = rememberMusicController(context)

    Column(modifier = Modifier.fillMaxSize()) {
        // The TabRow to display the tabs
        PrimaryTabRow(selectedTabIndex = pagerState.currentPage) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = { Text(text = title) }
                )
            }
        }

        // The HorizontalPager that contains the content for each tab
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) { page ->
            // Display the appropriate screen based on the selected page
            when (page) {
                0 -> SongListScreen(
                    onSongClick = { song ->
                        // Prepare and play selected song
                        controller?.setMediaItem(
                            MediaItem.fromUri(
                                Uri.fromFile(
                                    File(song.path)
                                )
                            )
                        )
                        controller?.prepare()
                        controller?.playWhenReady = true
                    },
                    controller = controller
                )
                1 -> PlaylistScreen(
                    onCreatePlaylist = {

                    }
                )
            }
        }
    }
}