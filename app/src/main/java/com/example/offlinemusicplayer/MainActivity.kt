package com.example.offlinemusicplayer

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.offlinemusicplayer.presentation.activities.ProfileActivity
import com.example.offlinemusicplayer.presentation.activities.SettingsActivity
import com.example.offlinemusicplayer.presentation.dialogs.CommonDialog
import com.example.offlinemusicplayer.presentation.main.MainVM
import com.example.offlinemusicplayer.presentation.mini_player_bar.MiniPlayerBar
import com.example.offlinemusicplayer.presentation.navigation.RootNavHost
import com.example.offlinemusicplayer.presentation.navigation.Screens
import com.example.offlinemusicplayer.presentation.now_playing_detail.NowPlayingDetail
import com.example.offlinemusicplayer.presentation.providers.LocalBottomScrollBehavior
import com.example.offlinemusicplayer.presentation.providers.LocalScrollBehavior
import com.example.offlinemusicplayer.ui.theme.OfflineMusicPlayerTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    var isPermissionGranted by mutableStateOf<Boolean>(false)
    var showSettings by mutableStateOf<Boolean?>(null)

    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        // Android 13+
        arrayOf(Manifest.permission.READ_MEDIA_AUDIO)
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        // Android 10, 11, 12
        // WRITE permission is not needed here, it's handled via RecoverableSecurityException
        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    } else {
        // Android 9 (Pie) and below
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            OfflineMusicPlayerTheme {

                val isDarkTheme = isSystemInDarkTheme()
                val primaryArgb = MaterialTheme.colorScheme.primary.toArgb()
                val backgroundArgb = MaterialTheme.colorScheme.background.toArgb()

                SideEffect {
                    enableEdgeToEdge(
                        statusBarStyle = SystemBarStyle.auto(
                            primaryArgb,
                            primaryArgb,
                        ) {
                            // This lambda determines whether to use dark icons based on the background luminance
                            // Return true for dark icons on a light background, false for light icons on a dark background
                            !isDarkTheme
                        },
                        navigationBarStyle = SystemBarStyle.auto(
                            backgroundArgb,
                            backgroundArgb,
                        )
                    )
                }

                isPermissionGranted = checkIfPermissionGranted()
                if (!isPermissionGranted) {
                    requestStoragePermission()
                }

                if(showSettings == true) {
                    SettingsDialog()
                } else if(showSettings == false) {
                    PermissionRationaleDialog(permissions)
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
        val context = LocalContext.current
        val rootNavController = rememberNavController()
        val mainNavController = rememberNavController()

        val viewModel = hiltViewModel<MainVM>()
        val currentSong by viewModel.currentMedia.collectAsStateWithLifecycle()
        val newlyAddedSongCount by viewModel.newlyAddedSongCount.collectAsStateWithLifecycle()

        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        var isSheetVisible by remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()

        val rootNavBackStackEntry by rootNavController.currentBackStackEntryAsState()
        val rootCurrentRoute: Screens? = remember(rootNavBackStackEntry) {
            Screens.fromRoute(rootNavBackStackEntry?.destination?.route)
        }
        val mainNavBackStackEntry by mainNavController.currentBackStackEntryAsState()
        val mainCurrentRoute: Screens? = remember(mainNavBackStackEntry) {
            Screens.fromRoute(mainNavBackStackEntry?.destination?.route)
        }

//        val showBackButton = currentRoute !is Screens.Home
        val showTopBar = mainCurrentRoute !is Screens.PlaylistDetail && rootCurrentRoute !is Screens.NowPlayingQueue
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
                        rootNavController.navigate(it)
                    }
                )
            }
        }

        BackHandler {
            rootNavController.popBackStack()
        }

        LaunchedEffect(newlyAddedSongCount) {
            if(newlyAddedSongCount > 0) {
                withContext(Dispatchers.Main){
                    val message = "$newlyAddedSongCount" + if(newlyAddedSongCount == 1) {
                        " song added"
                    } else {
                        " songs added"
                    }
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(mergedScrollConnection),
            topBar = {
                if(showTopBar) {
                    TopAppBar(
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
                            titleContentColor = MaterialTheme.colorScheme.onPrimary,
                            scrolledContainerColor = MaterialTheme.colorScheme.primary
                        ),
                        actions = {
                            Screens.drawerMenuItems.forEach { item ->
                                IconButton(
                                    onClick = {
                                        when (item.screen) {
                                            Screens.Profile -> {
                                                startActivity(
                                                    Intent(
                                                        this@MainActivity,
                                                        ProfileActivity::class.java
                                                    )
                                                )
                                            }
                                            Screens.Settings -> {
                                                startActivity(
                                                    Intent(
                                                        this@MainActivity,
                                                        SettingsActivity::class.java
                                                    )
                                                )
                                            }
                                            else -> rootNavController.navigate(item.screen)
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = item.imageVector,
                                        contentDescription = item.label,
                                        tint = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }
                        }
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
                                selected = rootCurrentRoute == item.screen,
                                onClick = {
                                    rootNavController.navigate(item.screen) {
                                        // This is the key to independent back stacks
                                        popUpTo(rootNavController.graph.findStartDestination().id) {
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
                    navController = rootNavController,
                    mainNavController = mainNavController,
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
                colors = ButtonDefaults.textButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = "Grant Storage permission",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }

    fun requestStoragePermission(permissions: Array<String> = this.permissions) {

        val permissionToRationale = permissions.first()

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissionToRationale)) {
            // The user has denied the permission before, but not "Don't ask again".
            // Show a rationale dialog.
            showSettings = false
        } else {
            // This is the first time, or the user has selected "Don't ask again".
            // The launcher will handle the "Don't ask again" case in its result.
            requestMultiplePermissions.launch(permissions)
        }
    }

    @Composable
    fun PermissionRationaleDialog(permissions: Array<String>) {
        CommonDialog(
            title = "Permission Required",
            description = "This app needs access to your storage to find and play music files.",
            positiveText = "Grant",
            onPositiveClick = {
                requestMultiplePermissions.launch(permissions)
            },
            onDismiss = {},
            dismissable = false
        )
    }

    @Composable
    fun SettingsDialog() {
        CommonDialog(
            title = "Permission Denied",
            description = "You have permanently denied the storage permission. To fetch the music file, please enable it from the settings.",
            positiveText = "Settings",
            onPositiveClick = {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            },
            onDismiss = {},
            dismissable = false
        )
    }

    private var onDeletePermissionGranted: (() -> Unit)? = null

    private val recoverableSecurityPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            onDeletePermissionGranted?.invoke()
        } else {
            Toast.makeText(this, "File cannot be deleted. Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    fun launchRecoverableSecurityPermission(intentSenderRequest: IntentSenderRequest, onPermissionGranted: () -> Unit) {
        onDeletePermissionGranted = onPermissionGranted
        recoverableSecurityPermissionLauncher.launch(intentSenderRequest)
    }

    private val requestMultiplePermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->

        val isGranted = permissions.entries.all { it.value }

        if (isGranted) {
            isPermissionGranted = true
            showSettings = null
        } else {
            isPermissionGranted = false
            // Check if any permission was permanently denied.
            val permanentlyDenied = permissions.entries.any {
                !it.value && !ActivityCompat.shouldShowRequestPermissionRationale(this, it.key)
            }

            if (permanentlyDenied) {
                showSettings = true
            } else {
                Toast.makeText(
                    this,
                    "Storage permission is required to play music.",
                    Toast.LENGTH_SHORT
                ).show()
            }
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

    fun checkIfWriteAccessGranted(): Boolean {
        val checkVal2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return checkVal2 == PackageManager.PERMISSION_GRANTED
    }
}