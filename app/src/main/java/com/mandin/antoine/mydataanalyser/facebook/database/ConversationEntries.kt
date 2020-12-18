package com.mandin.antoine.mydataanalyser.facebook.database

import android.provider.BaseColumns._ID
import com.mandin.antoine.mydataanalyser.utils.database.BaseColumns

object ConversationEntries : BaseColumns {
    const val TABLE_NAME = "conversations"
    const val COLUMN_TITLE = "title"
    const val COLUMN_PATH = "path"
    const val COLUMN_MESSAGE_COUNT = "message_count"
    const val COLUMN_PHOTO_COUNT = "message_count"
    const val COLUMN_FIRST_MESSAGE_DATE = "first_message_date"

    override fun getSqlCreateEntries(): String {
        return "CREATE TABLE $TABLE_NAME (" +
                "$_ID INTEGER PRIMARY KEY, " +
                "$COLUMN_TITLE TEXT, " +
                "$COLUMN_PATH TEXT, " +
                "$COLUMN_MESSAGE_COUNT INTEGER, " +
                "$COLUMN_PHOTO_COUNT INTEGER, " +
                "$COLUMN_FIRST_MESSAGE_DATE DATE)"
    }

    override fun getSqlDeleteEntries(): String {
        return "DROP TABLE IF EXISTS $TABLE_NAME"
    }
}