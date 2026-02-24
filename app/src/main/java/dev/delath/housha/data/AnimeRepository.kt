package dev.delath.housha.data

import dev.delath.housha.model.Anime
import dev.delath.housha.network.AnilistApiService
import dev.delath.housha.network.GraphQLRequest
import dev.delath.housha.util.SeasonUtils
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnimeRepository @Inject constructor(
    private val apiService: AnilistApiService,
    private val dao: SubscribedAnimeDao
) {

    private val seasonQuery = """
        query (${'$'}season: MediaSeason, ${'$'}year: Int) {
          Page(page: 1, perPage: 50) {
            media(season: ${'$'}season, seasonYear: ${'$'}year, type: ANIME, sort: POPULARITY_DESC) {
              id
              title { romaji }
              coverImage { extraLarge }
              episodes
              averageScore
              status
              studios(isMain: true) { nodes { name isAnimationStudio } }
              nextAiringEpisode { episode timeUntilAiring }
            }
          }
        }
    """.trimIndent()

    suspend fun fetchCurrentSeason(): List<Anime> {
        val season = SeasonUtils.currentSeason().name
        val year = SeasonUtils.currentSeasonYear()
        val request = GraphQLRequest(
            query = seasonQuery,
            variables = mapOf("season" to season, "year" to year)
        )
        val response = apiService.fetchSeasonAnime(request)
        return response.data.page.media.map { media ->
            val nextEpisode = media.nextAiringEpisode
            val nextEpisodeAt = nextEpisode?.let {
                System.currentTimeMillis() / 1000 + it.timeUntilAiring
            }
            Anime(
                id = media.id,
                title = media.title.romaji,
                imageUrl = media.coverImage.extraLarge,
                episodeCount = media.episodes ?: 0,
                nextEpisodeAt = nextEpisodeAt,
                nextEpisodeNumber = nextEpisode?.episode,
                averageScore = media.averageScore,
                status = media.status,
                studio = media.studios?.nodes
                    ?.firstOrNull { it.isAnimationStudio }?.name
                    ?: media.studios?.nodes?.firstOrNull()?.name
            )
        }
    }

    fun getSubscribed(): Flow<List<Anime>> = dao.getAll()

    fun getSubscribedCount(): Flow<Int> = dao.getCount()


    suspend fun subscribe(anime: Anime) = dao.insert(anime)

    suspend fun unsubscribe(anime: Anime) = dao.delete(anime)

    suspend fun getAllSubscribedOnce(): List<Anime> = dao.getAllOnce()
}

