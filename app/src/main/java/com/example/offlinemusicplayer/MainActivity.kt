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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.example.offlinemusicplayer.presentation.navigation.RootNavHost
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

                    BackHandler {
                        navController.popBackStack()
                    }

                    RootNavHost(navController)
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