package ru.kpfu.itis.core.data.local.cache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.kpfu.itis.core.data.local.cache.entity.SearchCache

@Dao
interface SearchCacheDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchCache(cache: SearchCache)

    @Query("SELECT * FROM search_cache WHERE query = :query")
    suspend fun getSearchCache(query: String): SearchCache?

    @Query("DELETE FROM search_cache WHERE cached_at < :timeLimit")
    suspend fun deleteExpiredCache(timeLimit: Long)

    @Query("DELETE FROM search_cache")
    suspend fun clearAll()
}