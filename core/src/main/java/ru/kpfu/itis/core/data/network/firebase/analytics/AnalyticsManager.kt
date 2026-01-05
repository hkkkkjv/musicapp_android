package ru.kpfu.itis.core.data.network.firebase.analytics

import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.Trace
import javax.inject.Inject

class AnalyticsManager @Inject constructor(
    private val analytics: FirebaseAnalytics,
    private val crashlytics: FirebaseCrashlytics,
    private val performance: FirebasePerformance
) {

    private val traces = mutableMapOf<String, Trace>()

    fun logScreenOpened(screenName: String, screenClass: String) {
        val parameters = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenClass)
        }
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, parameters)
        Log.d("Analytics", "Screen opened: $screenName")
    }

    fun logSongDetailsOpened(songId: String, songTitle: String) {
        val parameters = Bundle().apply {
            this.putString("song_id", songId)
            this.putString("song_title", songTitle)
        }
        analytics.logEvent("song_details_opened", parameters)
    }

    fun logSearchQuery(query: String) {
        val parameters = Bundle().apply {
            putString(FirebaseAnalytics.Param.SEARCH_TERM, query)
            putInt("query_length", query.length)
        }
        analytics.logEvent("search_query", parameters)
    }

    fun logSongSelected(songId: String, songTitle: String) {
        val parameters = Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_ID, songId)
            putString(FirebaseAnalytics.Param.ITEM_NAME, songTitle)
            putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "song")
        }
        analytics.logEvent("song_selected", parameters)
    }

    fun logSearchError(errorMessage: String) {
        val parameters = Bundle().apply {
            this.putString("error", errorMessage)
        }
        analytics.logEvent("search_error", parameters)
    }

    fun logReviewCreated(reviewId: String, songId: String, rating: Float) {
        val parameters = Bundle().apply {
            putString("review_id", reviewId)
            putString("song_id", songId)
            putFloat("rating", rating)
            putString(FirebaseAnalytics.Param.SCREEN_NAME, "review_add")
        }
        analytics.logEvent("review_created", parameters)
        Log.d("Analytics", "Review created: $reviewId, rating: $rating")
    }

    fun logReviewDeleted(reviewId: String) {
        val parameters = Bundle().apply {
            putString("review_id", reviewId)
        }
        analytics.logEvent("review_deleted", parameters)
        Log.d("Analytics", "Review deleted: $reviewId")
    }

    fun startPerformanceTrace(traceName: String) {
        if (!traces.containsKey(traceName)) {
            val trace = performance.newTrace(traceName)
            trace.start()
            traces[traceName] = trace
            Log.d("Performance", "Trace started: $traceName")
        }
    }

    fun stopPerformanceTrace(traceName: String) {
        traces[traceName]?.let {
            it.stop()
            traces.remove(traceName)
            Log.d("Performance", "Trace stopped: $traceName")
        }
    }

    fun putTraceAttribute(traceName: String, attributeName: String, value: String) {
        traces[traceName]?.putAttribute(attributeName, value)
    }

    fun trackDataFetch(operationName: String, action: suspend () -> Unit) {
        startPerformanceTrace(operationName)
        try {
            Log.d("Performance", "Starting fetch: $operationName")
        } finally {
        }
    }

    fun logNonFatalException(exception: Exception, context: String = "") {
        crashlytics.recordException(exception)

        if (context.isNotEmpty()) {
            crashlytics.setCustomKey("error_context", context)
        }

        Log.e("Crashlytics", "Non-fatal exception logged", exception)
    }

    fun logLoadingError(screenName: String, errorMessage: String) {
        val parameters = Bundle().apply {
            putString("screen", screenName)
            putString("error", errorMessage)
        }
        analytics.logEvent("loading_error", parameters)

        logNonFatalException(
            Exception("Loading error on $screenName: $errorMessage"),
            context = screenName
        )

        Log.e("Analytics", "Loading error on $screenName: $errorMessage")
    }

    fun logOperationSuccess(operationName: String, durationMs: Long) {
        val parameters = Bundle().apply {
            putString("operation", operationName)
            putLong("duration_ms", durationMs)
        }
        analytics.logEvent("operation_success", parameters)
        Log.d("Analytics", "Operation '$operationName' completed in ${durationMs}ms")
    }
}