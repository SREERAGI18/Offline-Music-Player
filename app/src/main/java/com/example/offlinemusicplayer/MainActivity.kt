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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.core.content.ContextCompat
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.offlinemusicplayer.presentation.navigation.AppNavHost
import com.example.offlinemusicplayer.presentation.navigation.Screens
import com.example.offlinemusicplayer.ui.theme.OfflineMusicPlayerTheme
import dagger.hilt.android.AndroidEntryPoint

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
                    val navController = rememberNavController()
                    var query by rememberSaveable { mutableStateOf("") }
                    var isSearchActive by rememberSaveable { mutableStateOf(false) }

                    val focusRequester = remember { FocusRequester() }

                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route

                    val showBackButton = currentRoute != Screens.Main.route

                    LaunchedEffect(currentRoute) {
                        if(currentRoute == Screens.Main.route) {
                            if(isSearchActive) {
                                focusRequester.freeFocus()
                                isSearchActive = false
                                query = ""
                            }
                        } else if(currentRoute == Screens.Search.route) {
                            isSearchActive = true
                        }
                    }

                    BackHandler {
                        navController.popBackStack()
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
                                                tint = MaterialTheme.colorScheme.onPrimary // Ensure icon is visible
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
                                                        if (navController.currentBackStackEntry?.destination?.route != Screens.Search.route) {
                                                            navController.navigate(Screens.Search.route)
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
                                    titleContentColor = MaterialTheme.colorScheme.onPrimary // Also set this
                                ),
                                actions = {
                                    if (!isSearchActive) {
                                        IconButton(onClick = { isSearchActive = true }) {
                                            Icon(
                                                imageVector = Icons.Default.Search,
                                                contentDescription = "Search",
                                                tint = MaterialTheme.colorScheme.onPrimary // Ensure icon is visible
                                            )
                                        }
                                    }
                                }
                            )                        },
                    ) { innerPadding ->
                        AppNavHost(
                            navController = navController,
                            modifier = Modifier.padding(innerPadding),
                            query = query
                        )
                    }
                } else {
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