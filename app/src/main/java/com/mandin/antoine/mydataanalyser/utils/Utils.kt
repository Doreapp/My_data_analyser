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
}