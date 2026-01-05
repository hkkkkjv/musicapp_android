package ru.kpfu.itis.core.data.local.cache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.kpfu.itis.core.data.local.cache.entity.CachedSong

@Dao
interface CachedSongDao {

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertSongs(songs: List<CachedSong>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSong(song: CachedSong)

    @Query("SELECT * FROM cached_songs WHERE id IN (:songIds)")
    suspend fun getSongsByIds(songIds: List<String>): List<CachedSong>

    @Query("SELECT * FROM cached_songs WHERE id = :id")
    suspend fun getSongById(id: String): CachedSong?

    @Query("DELETE FROM cached_songs WHERE cached_at < :timeLimit")
    suspend fun deleteExpiredCache(timeLimit: Long)

    @Query("DELETE FROM cached_songs")
    suspend fun clearAll()
}