package com.mandin.antoine.mydataanalyser.facebook.model.data

import java.util.*

/**
 * Abstract class containing useful methods for stats classes
 */
abstract class BaseStats {
    /**
     * Increment a value into a tree map
     * @param map the map, in which to increment [date] value
     * @param date The day for which increment the value
     */
    protected fun increment(map: TreeMap<Date, Int>, date: Date) {
        when (val value = map[date]) {
            null -> map[date] = 1
            else -> map[date] = value + 1
        }
    }

    /**
     * Increment a value into a tree map
     * @param map the map, in which to increment [str] value
     * @param str The key for which increment the value
     */
    protected fun increment(map: HashMap<String, Int>, str: String) {
        when (val value = map[str]) {
            null -> map[str] = 1
            else -> map[str] = value + 1
        }
    }
}