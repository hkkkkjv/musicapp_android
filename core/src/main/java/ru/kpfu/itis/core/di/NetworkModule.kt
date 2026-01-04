package ru.kpfu.itis.core.di

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import ru.kpfu.itis.core.BuildConfig
import ru.kpfu.itis.core.data.network.GeniusAuthInterceptor
import ru.kpfu.itis.core.data.network.RetrofitFactory
import ru.kpfu.itis.core.data.network.deezer.DeezerApi
import ru.kpfu.itis.core.data.network.genius.GeniusApi
import ru.kpfu.itis.core.di.qualifiers.DeezerRetrofit
import ru.kpfu.itis.core.di.qualifiers.GeniusRetrofit
import ru.kpfu.itis.core.di.qualifiers.GeniusToken
import javax.inject.Singleton

@Module
class NetworkModule {

    @Provides
    @Singleton
    @GeniusToken
    fun provideGeniusToken(): String =
        BuildConfig.GENIUS_ACCESS_TOKEN


    @Provides
    @Singleton
    fun provideGeniusAuthInterceptor(
        @GeniusToken token: String
    ): GeniusAuthInterceptor =
        GeniusAuthInterceptor { token }


    @Provides
    @Singleton
    fun provideRetrofitFactory(
        geniusAuthInterceptor: GeniusAuthInterceptor
    ): RetrofitFactory =
        RetrofitFactory(geniusAuthInterceptor)


    @Provides
    @Singleton
    @GeniusRetrofit
    fun provideGeniusRetrofit(factory: RetrofitFactory): Retrofit =
        factory.provideGeniusRetrofit()


    @Provides
    @Singleton
    @DeezerRetrofit
    fun provideDeezerRetrofit(factory: RetrofitFactory): Retrofit =
        factory.provideDeezerRetrofit()


    @Provides
    @Singleton
    fun provideGeniusApi(
        @GeniusRetrofit retrofit: Retrofit
    ): GeniusApi =
        retrofit.create(GeniusApi::class.java)


    @Provides
    @Singleton
    fun provideDeezerApi(
        @DeezerRetrofit retrofit: Retrofit
    ): DeezerApi =
        retrofit.create(DeezerApi::class.java)

}
