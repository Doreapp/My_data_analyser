package com.mandin.antoine.mydataanalyser.utils

import java.util.*


object Utils {
    const val ZIP_CONTENT_TYPE = "application/zip"

    /**
     * Format big numbers
     *
     * @param value number to format
     * @return formatted string
     */
    fun formatBigNumber(value: Int?): String {
        if (value == null)
            return "[null]"
        if (value > 1_000_000)
            return "" + (value / 10_00 + 5) / 10 / 10f + "M"
        if (value > 10_000)
            return "" + (value / 100 + 5) / 10 + "K"
        if (value > 1000)
            return "" + (value / 10 + 5) / 10 / 10f + "K"
        return "$value"
    }

    /**
     * Date comparator, sorting dates ascending
     */
    object ClassicDateComparator : Comparator<Date> {
        override fun compare(o1: Date?, o2: Date?): Int {
            if (o1 == null || o2 == null)
                return 0
            return o1.compareTo(o2)
        }

    }

    /**
     * From a [value] return a tow digits string.
     * Examples :
     * * 1  --> "01"
     * * 23 --> "23"
     * * 2345 --> "2345"
     * * 0.56 --> "00.56"
     *
     * @param value value to format
     * @return a tow digit (at least) string
     */
    fun toTowDigits(value: Long): String {
        return if (value < 10)
            "0$value"
        else
            "$value"
    }

    /**
     * Format a [duration] (as long) into a short string.
     *
     * @param duration duration in millisecond
     * @return formatted string
     */
    fun formatDurationUntilHour(duration: Long): String {
        val seconds: Long = (duration / 1000) % 60
        val minutes: Long = (duration / 60_000) % 60
        val hours: Long = duration / 3600_000L
        if (hours == 0L) {
            if (minutes == 0L) {
                return "${seconds}s"
            }
            return "${minutes}min ${toTowDigits(seconds)}s"
        }
        return "${hours}h${toTowDigits(minutes)}"
    }
}