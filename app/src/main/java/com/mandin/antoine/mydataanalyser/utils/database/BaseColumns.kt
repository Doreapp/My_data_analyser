package com.mandin.antoine.mydataanalyser.utils.database

import android.provider.BaseColumns

interface BaseColumns : BaseColumns {
    fun getSqlCreateEntries(): String

    fun getSqlDeleteEntries(): String
}