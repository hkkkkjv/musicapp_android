package ru.kpfu.itis.core.network.deezer

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.kpfu.itis.core.network.deezer.models.DeezerSearchResponse
import ru.kpfu.itis.core.network.deezer.models.DeezerTrackDetailsResponse

interface DeezerApi {
    @GET("search/track")
    suspend fun searchTracks(
        @Query("q") query: String
    ): DeezerSearchResponse

    @GET("track/{id}")
    suspend fun getTrack(
        @Path("id") id: Long
    ): DeezerTrackDetailsResponse
}