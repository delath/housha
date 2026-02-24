package dev.delath.housha.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subscribed_anime")
data class Anime(
    @PrimaryKey val id: Int,
    val title: String,
    val imageUrl: String,
    val episodeCount: Int,
    val nextEpisodeAt: Long? = null,  // Unix timestamp (seconds) of next airing episode
    val nextEpisodeNumber: Int? = null,
    val averageScore: Int? = null,   // 0-100
    val status: String? = null,      // RELEASING, FINISHED, etc.
    val studio: String? = null       // Main animation studio name
)
