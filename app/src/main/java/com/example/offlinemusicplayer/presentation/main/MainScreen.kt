package com.example.offlinemusicplayer.presentation.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.offlinemusicplayer.presentation.home.HomeVM
import com.example.offlinemusicplayer.presentation.navigation.MainNavHost
import com.example.offlinemusicplayer.presentation.navigation.Screens
import com.example.offlinemusicplayer.presentation.now_playing.NowPlayingBar
import com.example.offlinemusicplayer.presentation.now_playing_detail.NowPlayingDetail
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigate:(Screens) -> Unit
) {
    val viewModel = hiltViewModel<HomeVM>()
    val currentSong by viewModel.currentMedia.collectAsStateWithLifecycle()

    val navController = rememberNavController()
    var query by rememberSaveable { mutableStateOf("") }
    var isSearchActive by rememberSaveable { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute: Screens? = remember(navBackStackEntry) {
        Screens.fromRoute(navBackStackEntry?.destination?.route)
    }

    val showBackButton = currentRoute !is Screens.Home

    LaunchedEffect(currentRoute) {
        if(currentRoute is Screens.Home) {
            if(isSearchActive) {
                focusRequester.freeFocus()
                isSearchActive = false
                query = ""
            }
        } else if(currentRoute is Screens.Search) {
            isSearchActive = true
        }
    }

    LaunchedEffect(isSearchActive) {
        if (isSearchActive) {
            focusRequester.requestFocus()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                navigationIcon = {
                    if (showBackButton) {
                        IconButton(
                            onClick = {
                                navController.popBackStack()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                },
                title = {
                    if (isSearchActive) {
                        TextField(
                            value = query,
                            onValueChange = { query = it },
                            placeholder = { Text("Search songs...", color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester)
                                .onFocusChanged { focusState ->
                                    if (focusState.isFocused) {
                                        if (navController.currentBackStackEntry?.destination != Screens.Search) {
                                            navController.navigate(Screens.Search)
                                        }
                                    }
                                },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = { /* a soft keyboard might be hidden here */ }),
                            colors = TextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                                unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
                                cursorColor = MaterialTheme.colorScheme.onPrimary,
                                unfocusedContainerColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent
                            )
                        )
                    } else {
                        Text(
                            text = "Offline Music Player",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    if (!isSearchActive) {
                        IconButton(onClick = { isSearchActive = true }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            )                        },
    ) { innerPadding ->
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        var isSheetVisible by remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()

        if (isSheetVisible && currentSong != null) {
            ModalBottomSheet(
                onDismissRequest = {
                    scope.launch {
                        sheetState.hide()
                        isSheetVisible = false
                    }
                },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surface,
                dragHandle = null,
                modifier = Modifier.fillMaxSize()
            ) {
                NowPlayingDetail(
                    viewModel = viewModel,
                    onCollapse = {
                        scope.launch {
                            sheetState.hide()
                            isSheetVisible = false
                        }
                    },
                    onNavigate = onNavigate
                )
            }
        }
        Box(
            modifier = Modifier.padding(innerPadding),
            contentAlignment = Alignment.BottomCenter
        ) {
            MainNavHost(
                navController = navController,
                onNavigate = onNavigate,
                query = query
            )
            if (currentSong != null && !isSheetVisible) {
                NowPlayingBar(
                    viewModel = viewModel,
                    onClick = {
                        scope.launch {
                            isSheetVisible = true
                            sheetState.expand()
                        }
                    },
                )
            }
        }
    }
}