package ru.kpfu.itis.core.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import ru.kpfu.itis.core.utils.StringProvider
import javax.inject.Singleton

@Module(
    includes = [
        NetworkModule::class,
    ]
)
class CoreModule {
    @Provides
    @Singleton
    fun provideContext(application: Application):
            Context = application.applicationContext

    @Provides
    @Singleton
    fun provideStringProvider(context: Context): StringProvider = AppStringProvider(context)

}
