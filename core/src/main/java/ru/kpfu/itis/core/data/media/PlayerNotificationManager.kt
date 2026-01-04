package ru.kpfu.itis.core.data.media

interface PlayerNotificationManager {
    fun showNotification(songTitle: String, artistName: String, isPlaying: Boolean)
    fun dismissNotification()
}