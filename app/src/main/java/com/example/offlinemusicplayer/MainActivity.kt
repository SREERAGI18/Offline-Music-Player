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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.offlinemusicplayer.presentation.navigation.RootNavHost
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

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute: Screens? = remember(navBackStackEntry) {
            Screens.fromRoute(navBackStackEntry?.destination?.route)
        }

        BackHandler {
            navController.popBackStack()
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
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

        ) { innerPadding ->
            RootNavHost(
                navController = navController,
                modifier = Modifier.padding(innerPadding)
            )
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