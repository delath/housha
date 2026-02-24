package dev.delath.housha

import org.junit.Assert.assertEquals
import org.junit.Test

class CountdownFormatTest {

    private fun formatCountdown(seconds: Long): String {
        val days    = seconds / 86400
        val hours   = (seconds % 86400) / 3600
        val minutes = (seconds % 3600) / 60
        return when {
            days > 0  -> "${days}d ${hours}h"
            hours > 0 -> "${hours}h ${minutes}m"
            else      -> "${minutes}m"
        }
    }

    @Test
    fun `days and hours display correctly`() {
        assertEquals("2d 4h", formatCountdown(2 * 86400 + 4 * 3600 + 30 * 60))
    }

    @Test
    fun `hours and minutes display when under a day`() {
        assertEquals("5h 30m", formatCountdown(5 * 3600 + 30 * 60))
    }

    @Test
    fun `minutes only display when under an hour`() {
        assertEquals("45m", formatCountdown(45 * 60))
    }

    @Test
    fun `zero minutes shows 0m`() {
        assertEquals("0m", formatCountdown(0))
    }
}

