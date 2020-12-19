package com.mandin.antoine.mydataanalyser.utils.database

import android.provider.BaseColumns

/**
 * Interface used by Databases to implements a table columns
 */
interface BaseColumns : BaseColumns {
    /**
     * @return a SQL String creating the table
     */
    fun getSqlCreateEntries(): String

    /**
     * @return a SQL String deleting the table
     */
    fun getSqlDeleteEntries(): String
}