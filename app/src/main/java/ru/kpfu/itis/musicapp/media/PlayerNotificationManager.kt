package ru.kpfu.itis.musicapp.media

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import ru.kpfu.itis.core.media.PlayerNotificationManager
import ru.kpfu.itis.musicapp.MainActivity
import ru.kpfu.itis.musicapp.R

class PlayerNotificationManagerImpl(private val context: Context) : PlayerNotificationManager {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val channelId = "music_player_channel"
    private val notificationId = 1

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            channelId,
            "Music Player",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Shows currently playing song"
        }
        notificationManager.createNotificationChannel(channel)
    }

    override fun showNotification(songTitle: String, artistName: String, isPlaying: Boolean) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(songTitle)
            .setContentText(artistName)
            .setSmallIcon(R.drawable.ic_media_play)
            .setContentIntent(pendingIntent)
            .setOngoing(isPlaying)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        notificationManager.notify(notificationId, notification)
    }

    override fun dismissNotification() {
        notificationManager.cancel(notificationId)
    }
}