package ru.kpfu.itis.core.network

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class GeniusAuthInterceptor @Inject constructor(
    private val geniusTokenProvider: () -> String
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val newRequest = if (originalRequest.url.host.contains("genius")) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer ${geniusTokenProvider()}")
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(newRequest)
    }
}