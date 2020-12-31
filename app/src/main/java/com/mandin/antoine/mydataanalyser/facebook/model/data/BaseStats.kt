package com.mandin.antoine.mydataanalyser.facebook.model.data

import java.util.*

abstract class BaseStats {
    protected fun increment(map: TreeMap<Date, Int>, date: Date) {
        when (val value = map[date]) {
            null -> map[date] = 1
            else -> map[date] = value + 1
        }
    }

    protected fun increment(map: HashMap<String, Int>, str: String) {
        when (val value = map[str]) {
            null -> map[str] = 1
            else -> map[str] = value + 1
        }
    }
}