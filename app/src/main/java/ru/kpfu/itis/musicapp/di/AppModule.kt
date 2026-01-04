package ru.kpfu.itis.musicapp.di

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.kpfu.itis.core.data.media.PlayerNotificationManager
import ru.kpfu.itis.musicapp.media.PlayerNotificationManagerImpl
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    fun providePlayerNotificationManager(context: Context): PlayerNotificationManager =
        PlayerNotificationManagerImpl(context)

}