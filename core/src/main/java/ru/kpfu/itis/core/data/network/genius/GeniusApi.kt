package ru.kpfu.itis.core.data.network.genius

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url
import ru.kpfu.itis.core.data.network.genius.models.GeniusSearchResponse
import ru.kpfu.itis.core.data.network.genius.models.GeniusSongDetailsResponse

interface GeniusApi {
    @GET("/search")
    suspend fun searchSongs(
        @Query("q") query: String
    ): GeniusSearchResponse

    @GET("/songs/{id}")
    suspend fun getSongDetails(
        @Path("id") songId: Long,
    ): GeniusSongDetailsResponse

    @GET
    @Headers("Accept: text/html")
    suspend fun getSongHtml(
        @Url url: String
    ): ResponseBody
}