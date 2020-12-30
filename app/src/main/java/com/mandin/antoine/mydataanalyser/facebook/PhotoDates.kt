package com.mandin.antoine.mydataanalyser.facebook

import java.util.*
import kotlin.collections.HashMap

/**
 * Collections of dated photos, linking uri to date
 */
object PhotoDates {
    private val photoDates = HashMap<String, Date>()

    /**
     * Put the [date] of a photo (represented by [uri]) into the collection
     */
    fun putFromUri(uri: String, date: Long) {
        val key = uri.substring(uri.lastIndexOf("/") + 1)
        photoDates[key] = Date(date * 1000L)
    }

    /**
     * Retrieve the date of a photo from its name (uri's end)
     */
    fun getFromName(name: String): Date? {
        return photoDates[name]
    }
}