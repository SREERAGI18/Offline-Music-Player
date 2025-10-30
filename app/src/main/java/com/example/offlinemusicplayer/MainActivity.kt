package com.example.offlinemusicplayer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.offlinemusicplayer.presentation.main.MainVM
import com.example.offlinemusicplayer.presentation.mini_player_bar.MiniPlayerBar
import com.example.offlinemusicplayer.presentation.navigation.RootNavHost
import com.example.offlinemusicplayer.presentation.navigation.Screens
import com.example.offlinemusicplayer.presentation.now_playing_detail.NowPlayingDetail
import com.example.offlinemusicplayer.presentation.providers.LocalBottomScrollBehavior
import com.example.offlinemusicplayer.presentation.providers.LocalScrollBehavior
import com.example.offlinemusicplayer.ui.theme.OfflineMusicPlayerTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    var isPermissionGranted by mutableStateOf(false)

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OfflineMusicPlayerTheme {

                isPermissionGranted = checkIfPermissionGranted()
                if (!isPermissionGranted) {
                    requestStoragePermission()
                }

                if(isPermissionGranted) {
                    MainBody()
                } else {
                    RequestPermissionButton()
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun MainBody() {
        val navController = rememberNavController()

        val viewModel = hiltViewModel<MainVM>()
        val currentSong by viewModel.currentMedia.collectAsStateWithLifecycle()

        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        var isSheetVisible by remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute: Screens? = remember(navBackStackEntry) {
            Screens.fromRoute(navBackStackEntry?.destination?.route)
        }

//        val showBackButton = currentRoute !is Screens.Home
        val showTopBar = currentRoute !is Screens.PlaylistDetail && currentRoute !is Screens.NowPlayingQueue
        val topBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
        val bottomBarScrollBehavior = BottomAppBarDefaults.exitAlwaysScrollBehavior()

        val mergedScrollConnection = remember {
            object : NestedScrollConnection {
                override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
//                    val bottomConsumed = bottomBarScrollBehavior.nestedScrollConnection
//                        .onPreScroll(available, source)
                    val topConsumed = topBarScrollBehavior.nestedScrollConnection
                        .onPreScroll(available, source)
                    return topConsumed
                }

                override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
//                    val bottomConsumed = bottomBarScrollBehavior.nestedScrollConnection
//                        .onPostScroll(consumed, available, source)
                    val topConsumed = topBarScrollBehavior.nestedScrollConnection
                        .onPostScroll(consumed, available, source)
                    return topConsumed
                }
            }
        }

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
                    onNavigate = {
                        navController.navigate(it)
                    }
                )
            }
        }

        BackHandler {
            navController.popBackStack()
        }

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(mergedScrollConnection),
            topBar = {
                if(showTopBar) {
                    TopAppBar(
//                        navigationIcon = {
//                            if (showBackButton) {
//                                IconButton(
//                                    onClick = {
//                                        navController.popBackStack()
//                                    }
//                                ) {
//                                    Icon(
//                                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
//                                        contentDescription = "Back",
//                                        tint = MaterialTheme.colorScheme.onPrimary
//                                    )
//                                }
//                            }
//                        },
                        scrollBehavior = topBarScrollBehavior,
                        title = {
                            Text(
                                text = "Offline Music Player",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                    )
                }
            },
            bottomBar = {
                Column {
                    if (currentSong != null) {
                        MiniPlayerBar(
                            viewModel = viewModel,
                            onClick = {
                                scope.launch {
                                    isSheetVisible = true
                                    sheetState.expand()
                                }
                            },
                        )
                    }
                    BottomAppBar(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        scrollBehavior = bottomBarScrollBehavior
                    ) {
                        Screens.bottomMenuItems.forEach { item ->
                            NavigationBarItem(
                                selected = currentRoute == item.screen,
                                onClick = {
                                    navController.navigate(item.screen) {
                                        // This is the key to independent back stacks
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = item.imageVector,
                                        contentDescription = item.label
                                    )
                                },
                                label = {
                                    Text(
                                        text = item.label,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    indicatorColor = Color.Transparent
                                )
                            )
                        }
                    }
                }
            }

        ) { innerPadding ->
            CompositionLocalProvider(
                LocalScrollBehavior provides topBarScrollBehavior,
                LocalBottomScrollBehavior provides bottomBarScrollBehavior,
            ) {
                RootNavHost(
                    navController = navController,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }

    @Composable
    private fun RequestPermissionButton() {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize(),
        ) {
            TextButton(
                onClick = {
                    requestStoragePermission()
                },
                modifier = Modifier.clickable {
                    requestStoragePermission()
                },
                colors = ButtonDefaults.textButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = "Grant Storage permission",
                    style = MaterialTheme.typography.displayMedium
                )
            }
        }
    }

    private fun requestStoragePermission() {

        val permissions = if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
            )
        } else {
            arrayOf(
                Manifest.permission.READ_MEDIA_AUDIO,
            )
        }

        requestMultiplePermissions.launch(permissions)
    }

    private val requestMultiplePermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->

        var permissionGranted = true

        permissions.entries.forEach {
            Log.d("DEBUG", "${it.key} = ${it.value}")
            if(!it.value) {
                permissionGranted = false
            }
        }

        if (permissionGranted) {
            isPermissionGranted = true
        } else {
            isPermissionGranted = false
            Toast.makeText(
                this,
                "Storage permission is required to play music.",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    private fun checkIfPermissionGranted():Boolean {

        val requiredPermission1 = if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            Manifest.permission.READ_EXTERNAL_STORAGE
        } else {
            Manifest.permission.READ_MEDIA_AUDIO
        }
        val checkVal1 = ContextCompat.checkSelfPermission(this, requiredPermission1)

        return checkVal1 == PackageManager.PERMISSION_GRANTED
    }
}