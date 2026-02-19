package com.example.offlinemusicplayer

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.StrictMode
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.memory.MemoryCache
import coil3.util.DebugLogger
import com.example.offlinemusicplayer.util.Constants.DISK_CACHE_SIZE
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MusicApp :
    Application(),
    SingletonImageLoader.Factory {
    override fun onCreate() {
        super.onCreate()
        val channel =
            NotificationChannel(
                "music_channel",
                "Music Playback",
                NotificationManager.IMPORTANCE_LOW,
            )
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
            .createNotificationChannel(channel)

        if (BuildConfig.DEBUG) {
            enableStrictMode()
        }
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader =
        ImageLoader
            .Builder(this)
            .memoryCache {
                MemoryCache
                    .Builder()
                    .maxSizePercent(this)
                    .build()
            }.diskCache {
                DiskCache
                    .Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizeBytes(DISK_CACHE_SIZE)
                    .build()
            }.logger(DebugLogger())
//            .respectCacheHeaders(false)
            .build()

    private fun enableStrictMode() {
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy
                .Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                // .detectAll() // For all detectable thread policy violations
                .penaltyLog() // Log violations to Logcat
                // .penaltyDeath() // Crash the app on violation
                .build(),
        )

        StrictMode.setVmPolicy(
            StrictMode.VmPolicy
                .Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .build(),
        )
    }
}
