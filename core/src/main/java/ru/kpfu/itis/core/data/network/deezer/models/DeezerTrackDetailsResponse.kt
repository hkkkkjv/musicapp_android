package ru.kpfu.itis.core.data.network.deezer.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeezerTrackDetailsResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("title")
    val title: String,
    @SerialName("artist")
    val artist: DeezerArtistDto,
    @SerialName("album")
    val album: DeezerAlbumDto? = null,
    @SerialName("preview")
    val preview: String? = null,
    @SerialName("duration")
    val duration: Int? = null,
    @SerialName("rank")
    val rank: Int? = null,
    @SerialName("release_date")
    val releaseDate: String? = null
)
