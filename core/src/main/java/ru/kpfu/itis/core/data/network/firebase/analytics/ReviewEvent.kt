package ru.kpfu.itis.core.data.network.firebase.analytics

sealed class ReviewEvent {
    data class Created(val reviewId: String, val songId: String, val rating: Float) : ReviewEvent()
    data class Deleted(val reviewId: String) : ReviewEvent()
    data class Updated(val reviewId: String, val rating: Float) : ReviewEvent()
}
