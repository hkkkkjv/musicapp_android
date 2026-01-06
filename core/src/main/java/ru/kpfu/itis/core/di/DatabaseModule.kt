package ru.kpfu.itis.core.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import ru.kpfu.itis.core.data.local.cache.AppDatabase
import ru.kpfu.itis.core.data.local.cache.dao.CachedSongDao
import ru.kpfu.itis.core.data.local.cache.dao.SearchCacheDao
import ru.kpfu.itis.core.utils.DatabaseConstants
import javax.inject.Singleton

@Module
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(application: Application): AppDatabase {
        return Room.databaseBuilder(
            application,
            AppDatabase::class.java,
            DatabaseConstants.DATABASE_NAME
        )
            .fallbackToDestructiveMigration(dropAllTables = false)
            .build()
    }

    @Provides
    @Singleton
    fun provideCachedSongDao(db: AppDatabase): CachedSongDao =
        db.cachedSongDao()


    @Provides
    @Singleton
    fun provideSearchCacheDao(db: AppDatabase): SearchCacheDao =
        db.searchCacheDao()

}