package com.mandin.antoine.mydataanalyser.facebook.database

import com.mandin.antoine.mydataanalyser.utils.database.BaseColumns

/**
 * Table entries for the Person Table
 *
 * contains
 * * `name` : Name of the person
 */
object PersonEntries : BaseColumns {
    const val TABLE_NAME = "persons"
    const val COLUMN_NAME = "name"

    override fun getSqlCreateEntries(): String {
        return "CREATE TABLE $TABLE_NAME (" +
                "${android.provider.BaseColumns._ID} INTEGER PRIMARY KEY, " +
                "$COLUMN_NAME TEXT)"
    }

    override fun getSqlDeleteEntries(): String {
        return "DROP TABLE IF EXISTS $TABLE_NAME;"
    }

}