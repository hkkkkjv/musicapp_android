package ru.kpfu.itis.core.data.local.cache

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.kpfu.itis.core.data.local.cache.dao.CachedSongDao
import ru.kpfu.itis.core.data.local.cache.dao.SearchCacheDao
import ru.kpfu.itis.core.data.local.cache.entity.CachedSong
import ru.kpfu.itis.core.data.local.cache.entity.SearchCache
import ru.kpfu.itis.core.utils.DatabaseConstants

@Database(
    entities = [
        CachedSong::class,
        SearchCache::class
    ],
    version = DatabaseConstants.DATABASE_VERSION,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun cachedSongDao(): CachedSongDao
    abstract fun searchCacheDao(): SearchCacheDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}