package dev.delath.housha.util

import java.time.LocalDate
import java.time.Month

object SeasonUtils {

    enum class AnilistSeason { WINTER, SPRING, SUMMER, FALL }

    fun currentSeason(): AnilistSeason = when (LocalDate.now().month) {
        Month.DECEMBER, Month.JANUARY, Month.FEBRUARY -> AnilistSeason.WINTER
        Month.MARCH,    Month.APRIL,   Month.MAY      -> AnilistSeason.SPRING
        Month.JUNE,     Month.JULY,    Month.AUGUST   -> AnilistSeason.SUMMER
        else                                           -> AnilistSeason.FALL
    }

    /** December is counted as the start of the following year's winter season. */
    fun currentSeasonYear(): Int = LocalDate.now().let { date ->
        if (date.month == Month.DECEMBER) date.year + 1 else date.year
    }
}
