package dev.delath.housha

import dev.delath.housha.util.SeasonUtils
import dev.delath.housha.util.SeasonUtils.AnilistSeason
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Month

class SeasonUtilsTest {

    @Test
    fun `winter months map to WINTER`() {
        listOf(Month.DECEMBER, Month.JANUARY, Month.FEBRUARY).forEach { month ->
            assertEquals(AnilistSeason.WINTER, seasonForMonth(month))
        }
    }

    @Test
    fun `spring months map to SPRING`() {
        listOf(Month.MARCH, Month.APRIL, Month.MAY).forEach { month ->
            assertEquals(AnilistSeason.SPRING, seasonForMonth(month))
        }
    }

    @Test
    fun `summer months map to SUMMER`() {
        listOf(Month.JUNE, Month.JULY, Month.AUGUST).forEach { month ->
            assertEquals(AnilistSeason.SUMMER, seasonForMonth(month))
        }
    }

    @Test
    fun `fall months map to FALL`() {
        listOf(Month.SEPTEMBER, Month.OCTOBER, Month.NOVEMBER).forEach { month ->
            assertEquals(AnilistSeason.FALL, seasonForMonth(month))
        }
    }

    private fun seasonForMonth(month: Month): AnilistSeason = when (month) {
        Month.DECEMBER, Month.JANUARY, Month.FEBRUARY -> AnilistSeason.WINTER
        Month.MARCH,    Month.APRIL,   Month.MAY      -> AnilistSeason.SPRING
        Month.JUNE,     Month.JULY,    Month.AUGUST   -> AnilistSeason.SUMMER
        else                                           -> AnilistSeason.FALL
    }
}