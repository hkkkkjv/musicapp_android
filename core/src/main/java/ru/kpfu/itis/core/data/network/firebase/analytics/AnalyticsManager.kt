package ru.kpfu.itis.core.data.network.firebase.analytics

import android.os.Bundle
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import javax.inject.Inject

class AnalyticsManager @Inject constructor() {

    private val analytics: FirebaseAnalytics = Firebase.analytics

    fun logSongDetailsOpened(songId: String, songTitle: String) {
        val parameters = Bundle().apply {
            this.putString("song_id", songId)
            this.putString("song_title", songTitle)
        }
        analytics.logEvent("song_details_opened", parameters)
    }

    fun logLyricsOpened(songId: String) {
        val parameters = Bundle().apply {
            this.putString("song_id", songId)
        }
        analytics.logEvent("lyrics_opened", parameters)
    }

    fun logSearchQuery(query: String) {
        val parameters = Bundle().apply {
            this.putString("query", query)
        }
        analytics.logEvent("search_query", parameters)
    }

    fun logSongSelected(songId: String, songTitle: String) {
        val parameters = Bundle().apply {
            this.putString("song_id", songId)
            this.putString("song_title", songTitle)
        }
        analytics.logEvent("song_selected", parameters)
    }

    fun logSearchError(errorMessage: String) {
        val parameters = Bundle().apply {
            this.putString("error", errorMessage)
        }
        analytics.logEvent("search_error", parameters)
    }
}