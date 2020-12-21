package com.mandin.antoine.mydataanalyser.facebook

import java.util.*
import kotlin.collections.HashMap

object PhotoDates {
    private val photoDates = HashMap<String, Date>()

    fun putFromUri(uri: String, date: Long) {
        val key = uri.substring(uri.lastIndexOf("/") + 1)
        photoDates[key] = Date(date)
    }

    fun getFromName(name: String): Date? {
        return photoDates[name]
    }
}