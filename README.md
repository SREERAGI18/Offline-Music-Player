# 🎵 Offline Music Player

A modern **Music Player app** built with **Jetpack Compose** following **Clean Architecture** principles.  
The app uses **Media3**, **Room**, **Hilt**, and **Navigation-Compose** to provide a robust, scalable music experience.

---

## ✨ Features

- 🎶 **Play & Pause Music**
  - Browse all audio files from internal storage and SD card.
  - Tap a song to play instantly.
  - Media3 service handles playback with media-style notification.

- 📂 **Playlist Management**
  - Create playlists and store them in a Room database.
  - Add/remove songs from playlists.
  - View playlist details.

- 🌓 **Dark Mode**
  - UI adapts automatically to system dark/light theme.
  - Built with Material3 dynamic color schemes.

- 🧭 **Navigation**
  - Type-safe navigation using sealed class routes.
  - Two main screens:
    - **Song List Screen**
    - **Playlist Screen**

---

## 🏗️ Architecture

This project follows **Clean Architecture** with three layers:

```plaintext
com.lyrisync
├── domain
│ ├── model # Core entities (Song, Playlist)
│ ├── repository # Repository interfaces
│ └── usecase # Business logic
│
├── data
│ ├── local # Room entities & DAO
│ └── repository # Repository implementations
│
├── presentation
│ ├── navigation # Screen sealed class, NavHost
│ ├── songlist # SongListScreen + ViewModel
│ ├── playlist # PlaylistScreen + ViewModel
│
└── player # Media3 ExoPlayer + Media3 Service
```


---

## 🛠️ Tech Stack

- **UI**: [Jetpack Compose](https://developer.android.com/jetpack/compose)
- **Architecture**: Clean Architecture + MVVM
- **Navigation**: Navigation-Compose
- **DI**: [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
- **Database**: [Room](https://developer.android.com/training/data-storage/room)
- **Media Playback**: [Media3 ExoPlayer](https://developer.android.com/guide/topics/media/media3)
- **Coroutines & Flows**: For async and reactive state

---
