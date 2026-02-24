package dev.delath.housha.data

import androidx.room.*
import dev.delath.housha.model.Anime
import kotlinx.coroutines.flow.Flow

@Dao
interface SubscribedAnimeDao {

    @Query("SELECT * FROM subscribed_anime ORDER BY title ASC")
    fun getAll(): Flow<List<Anime>>

    @Query("SELECT COUNT(*) FROM subscribed_anime")
    fun getCount(): Flow<Int>

    @Query("SELECT * FROM subscribed_anime")
    suspend fun getAllOnce(): List<Anime>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(anime: Anime)

    @Delete
    suspend fun delete(anime: Anime)
}

