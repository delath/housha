package dev.delath.housha.network

import com.google.gson.annotations.SerializedName

data class AnilistResponse(
    @SerializedName("data") val data: Data
) {
    data class Data(
        @SerializedName("Page") val page: Page
    ) {
        data class Page(
            @SerializedName("media") val media: List<Media>
        ) {
            data class Media(
                @SerializedName("id") val id: Int,
                @SerializedName("title") val title: Title,
                @SerializedName("coverImage") val coverImage: CoverImage,
                @SerializedName("episodes") val episodes: Int?,
                @SerializedName("averageScore") val averageScore: Int?,
                @SerializedName("status") val status: String?,
                @SerializedName("studios") val studios: Studios?,
                @SerializedName("nextAiringEpisode") val nextAiringEpisode: NextAiringEpisode?
            ) {
                data class Title(
                    @SerializedName("romaji") val romaji: String
                )

                data class CoverImage(
                    @SerializedName("extraLarge") val extraLarge: String
                )

                data class NextAiringEpisode(
                    @SerializedName("episode") val episode: Int,
                    @SerializedName("timeUntilAiring") val timeUntilAiring: Long // seconds
                )

                data class Studios(
                    @SerializedName("nodes") val nodes: List<StudioNode>
                ) {
                    data class StudioNode(
                        @SerializedName("name") val name: String,
                        @SerializedName("isAnimationStudio") val isAnimationStudio: Boolean
                    )
                }
            }
        }
    }
}
