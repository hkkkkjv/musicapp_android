package ru.kpfu.itis.core.media

import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.HttpDataSource

@UnstableApi
class DeezerHttpDataSourceFactory : HttpDataSource.Factory {
    override fun createDataSource(): HttpDataSource {
        return DefaultHttpDataSource.Factory()
            .setDefaultRequestProperties(
                mapOf(
                    "User-Agent" to "Mozilla/5.0 (Linux; Android 13) AppleWebKit/537.36",
                    "Accept" to "*/*",
                    "Referer" to "https://www.deezer.com/"
                )
            )
            .createDataSource()
    }

    override fun setDefaultRequestProperties(defaultRequestProperties: Map<String, String>): HttpDataSource.Factory {
        return DefaultHttpDataSource.Factory()
            .setDefaultRequestProperties(
                mapOf(
                    "User-Agent" to "Mozilla/5.0 (Linux; Android 13) AppleWebKit/537.36",
                    "Accept" to "*/*",
                    "Referer" to "https://www.deezer.com/"
                )
            )
    }
}