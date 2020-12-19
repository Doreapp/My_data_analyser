package com.mandin.antoine.mydataanalyser.facebook.database

import android.provider.BaseColumns._ID
import com.mandin.antoine.mydataanalyser.utils.database.BaseColumns

/**
 * Database entries into the table **Conversations**
 *
 * Contains columns :
 * * `title` -              Title of the conversation
 * * `path` -               Path to the directory of the conversation folder
 * * `message_count` -      Count of messages in the conversation
 * * `photo_count`-         Count of photos in the conversation
 * * `first_message_date` - Date of the first message sent
 *
 * @see BaseColumns
 *
 */
object ConversationEntries : BaseColumns {
    const val TABLE_NAME = "conversations"
    const val COLUMN_TITLE = "title"
    const val COLUMN_PATH = "path"
    const val COLUMN_IS_STILL_PARTICIPANT = "is_still_participant"
    const val COLUMN_MESSAGE_COUNT = "message_count"
    const val COLUMN_PHOTO_COUNT = "photo_count"
    const val COLUMN_AUDIO_COUNT = "audio_count"
    const val COLUMN_GIF_COUNT = "gif_count"
    const val COLUMN_CREATION_DATE = "creation_date"

    object Participants : BaseColumns {
        const val TABLE_NAME = "conversation_participants"
        const val CONVERSATION_ID = "conversation_id"
        const val PERSON_ID = "person_id"

        override fun getSqlCreateEntries(): String {
            return "CREATE TABLE $TABLE_NAME (" +
                    "$CONVERSATION_ID INTEGER, " +
                    "$PERSON_ID INTEGER);"
        }

        override fun getSqlDeleteEntries(): String {
            return "DROP TABLE IF EXISTS $TABLE_NAME"
        }

    }


    override fun getSqlCreateEntries(): String {
        return "CREATE TABLE $TABLE_NAME (" +
                "$_ID INTEGER PRIMARY KEY, " +
                "$COLUMN_TITLE TEXT, " +
                "$COLUMN_PATH TEXT, " +
                "$COLUMN_IS_STILL_PARTICIPANT INTEGER, " +
                "$COLUMN_MESSAGE_COUNT INTEGER, " +
                "$COLUMN_PHOTO_COUNT INTEGER, " +
                "$COLUMN_AUDIO_COUNT INTEGER, " +
                "$COLUMN_GIF_COUNT INTEGER, " +
                "$COLUMN_CREATION_DATE INTEGER)"
    }

    override fun getSqlDeleteEntries(): String {
        return "DROP TABLE IF EXISTS $TABLE_NAME"
    }
}