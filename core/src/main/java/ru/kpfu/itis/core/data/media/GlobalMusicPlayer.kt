package ru.kpfu.itis.core.data.media

import android.content.Context
import android.util.Log
import androidx.annotation.OptIn
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory

class GlobalMusicPlayer private constructor(context: Context) {

    private val appContext = context.applicationContext
    private var exoPlayer: ExoPlayer? = null
    private var currentSongId: String? = null
    private var listeners = mutableListOf<PlayerListener>()

    interface PlayerListener {
        fun onPlaybackStateChanged(isPlaying: Boolean, duration: Long = 0L)
        fun onPositionChanged(position: Long)
        fun onError(error: String)
    }

    init {
        createPlayer()
    }

    @OptIn(UnstableApi::class)
    private fun createPlayer() {
        exoPlayer = ExoPlayer.Builder(appContext)
            .setMediaSourceFactory(
                DefaultMediaSourceFactory(appContext)
                    .setDataSourceFactory(DeezerHttpDataSourceFactory())
            )
            .build()

        exoPlayer?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                Log.d("GlobalMusicPlayer", "State: $playbackState")
                when (playbackState) {
                    Player.STATE_READY -> {
                        listeners.forEach { it.onPlaybackStateChanged(true, exoPlayer!!.duration) }
                    }
                    Player.STATE_ENDED -> {
                        listeners.forEach { it.onPlaybackStateChanged(false) }
                        stop()
                    }
                    else -> {}
                }
            }
        })
    }

    fun play(songId: String, previewUrl: String) {
        Log.d("GlobalMusicPlayer", "play() - songId=$songId, url=$previewUrl")

        if (currentSongId == songId && exoPlayer?.isPlaying == true) {
            Log.d("GlobalMusicPlayer", "Already playing this song")
            return
        }

        if (currentSongId != songId && exoPlayer?.isPlaying == true) {
            Log.d("GlobalMusicPlayer", "Stopping previous song")
            exoPlayer?.stop()
        }

        currentSongId = songId

        try {
            val mediaItem = MediaItem.fromUri(previewUrl.toUri())
            exoPlayer?.setMediaItem(mediaItem)
            exoPlayer?.prepare()
            exoPlayer?.play()
            Log.d("GlobalMusicPlayer", "✅ Playing")
        } catch (e: Exception) {
            Log.e("GlobalMusicPlayer", "Play error: ${e.message}")
            listeners.forEach { it.onError(e.message ?: "Unknown error") }
        }
    }

    fun pause() {
        Log.d("GlobalMusicPlayer", "pause()")
        exoPlayer?.pause()
        listeners.forEach { it.onPlaybackStateChanged(false) }
    }

    fun resume() {
        Log.d("GlobalMusicPlayer", "resume()")
        if (currentSongId != null) {
            exoPlayer?.play()
            listeners.forEach { it.onPlaybackStateChanged(true) }
        }
    }

    fun stop() {
        Log.d("GlobalMusicPlayer", "stop()")
        exoPlayer?.stop()
        currentSongId = null
        listeners.forEach { it.onPlaybackStateChanged(false) }
    }

    fun seekTo(position: Long) {
        exoPlayer?.seekTo(position)
    }

    fun getCurrentPosition(): Long = exoPlayer?.currentPosition ?: 0L
    fun getDuration(): Long = exoPlayer?.duration ?: 0L
    fun isPlaying(): Boolean = exoPlayer?.isPlaying ?: false
    fun getCurrentSongId(): String? = currentSongId

    fun addListener(listener: PlayerListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: PlayerListener) {
        listeners.remove(listener)
    }

    fun release() {
        Log.d("GlobalMusicPlayer", "release()")
        exoPlayer?.release()
        exoPlayer = null
        currentSongId = null
        listeners.clear()
    }

    companion object {
        @Volatile
        private var instance: GlobalMusicPlayer? = null

        fun getInstance(context: Context): GlobalMusicPlayer {
            return instance ?: synchronized(this) {
                instance ?: GlobalMusicPlayer(context).also { instance = it }
            }
        }

        fun release() {
            instance?.release()
            instance = null
        }
    }
}