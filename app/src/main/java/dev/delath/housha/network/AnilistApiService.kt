package dev.delath.housha.network

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AnilistApiService {
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/")
    suspend fun fetchSeasonAnime(@Body request: GraphQLRequest): AnilistResponse
}
