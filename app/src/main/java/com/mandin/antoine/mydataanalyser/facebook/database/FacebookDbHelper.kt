package com.mandin.antoine.mydataanalyser.facebook.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import android.provider.BaseColumns
import com.mandin.antoine.mydataanalyser.facebook.Persons
import com.mandin.antoine.mydataanalyser.facebook.model.Person
import com.mandin.antoine.mydataanalyser.facebook.model.data.ConversationBoxData
import com.mandin.antoine.mydataanalyser.facebook.model.data.ConversationData
import com.mandin.antoine.mydataanalyser.utils.Debug
import com.mandin.antoine.mydataanalyser.utils.database.Names
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

/**
 * Database Helper for Facebook database
 *
 * Queries :
 * * `Conversations` table
 * * `Person` table
 *
 * @see ConversationEntries
 * @see PersonEntries
 * @see Names.FACEBOOK_DB
 */
class FacebookDbHelper(context: Context?) :
    SQLiteOpenHelper(context, Names.FACEBOOK_DB, null, DATABASE_VERSION) {
    private val TAG = "FacebookDbHelper"

    companion object {
        // If you change the database schema, you must increment the database version.
        /**
         * Version of the database, link to a scheme
         */
        const val DATABASE_VERSION = 4
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(ConversationEntries.getSqlCreateEntries())
        db?.execSQL(ConversationEntries.Participants.getSqlCreateEntries())
        db?.execSQL(PersonEntries.getSqlCreateEntries())
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(ConversationEntries.getSqlDeleteEntries())
        db?.execSQL(ConversationEntries.Participants.getSqlDeleteEntries())
        db?.execSQL(PersonEntries.getSqlDeleteEntries())
        onCreate(db)
    }

    /**
     * Delete all the stored information
     */
    fun clear() {
        onUpgrade(writableDatabase, 0, DATABASE_VERSION)
    }

    fun findConversationById(id: Long): ConversationData? {
        val cursor = readableDatabase.rawQuery(
            "SELECT * FROM ${ConversationEntries.TABLE_NAME} " +
                    "WHERE ${BaseColumns._ID} = ?",
            arrayOf("$id")
        )

        var conversation: ConversationData? = null
        with(cursor) {
            if (moveToFirst()) {
                val title = getString(this, ConversationEntries.COLUMN_TITLE)
                val uriDescription = getString(this, ConversationEntries.COLUMN_URI)
                val isStillParticipant = getBoolean(this, ConversationEntries.COLUMN_IS_STILL_PARTICIPANT)
                val messageCount = getInt(this, ConversationEntries.COLUMN_MESSAGE_COUNT)
                val photoCount = getInt(this, ConversationEntries.COLUMN_PHOTO_COUNT)
                val audioCount = getInt(this, ConversationEntries.COLUMN_AUDIO_COUNT)
                val gifCount = getInt(this, ConversationEntries.COLUMN_GIF_COUNT)
                val date = getDate(this, ConversationEntries.COLUMN_CREATION_DATE)
                val participants = findParticipants(id)

                conversation =
                    ConversationData(
                        id, title,
                        Uri.parse(uriDescription),
                        participants,
                        isStillParticipant,
                        messageCount,
                        photoCount,
                        audioCount, gifCount,
                        date
                    )
            }
            close()
        }
        return conversation
    }

    /**
     * Read a the conversation box data
     */
    fun findConversationBoxData(): ConversationBoxData? {
        Debug.i(TAG, "findConversationBoxData")
        val cursor = readableDatabase.rawQuery(
            "SELECT * FROM ${ConversationEntries.TABLE_NAME}",
            null
        )

        val conversations = ArrayList<ConversationData>()
        with(cursor) {
            if (moveToFirst()) {
                do {
                    val id = getId(this)
                    val title = getString(this, ConversationEntries.COLUMN_TITLE)
                    val uriDescription = getString(this, ConversationEntries.COLUMN_URI)
                    val isStillParticipant = getBoolean(this, ConversationEntries.COLUMN_IS_STILL_PARTICIPANT)
                    val messageCount = getInt(this, ConversationEntries.COLUMN_MESSAGE_COUNT)
                    val photoCount = getInt(this, ConversationEntries.COLUMN_PHOTO_COUNT)
                    val audioCount = getInt(this, ConversationEntries.COLUMN_AUDIO_COUNT)
                    val gifCount = getInt(this, ConversationEntries.COLUMN_GIF_COUNT)
                    val date = getDate(this, ConversationEntries.COLUMN_CREATION_DATE)
                    val participants = findParticipants(id)

                    conversations.add(
                        ConversationData(
                            id, title,
                            Uri.parse(uriDescription),
                            participants,
                            isStillParticipant,
                            messageCount,
                            photoCount,
                            audioCount, gifCount,
                            date
                        )
                    )
                } while (moveToNext())
            }
            close()
        }
        if (conversations.isNotEmpty())
            return ConversationBoxData(
                null,
                null,
                conversations,
                null
            )
        return null
    }

    /**
     * Find the participants of a conversation
     *
     * @param idConversation id of the conversation
     */
    fun findParticipants(idConversation: Long): Set<Person> {
        Debug.i(TAG, "findParticipants : idConversation=$idConversation")
        val cursor = readableDatabase.rawQuery(
            "SELECT ${PersonEntries.TABLE_NAME}.${BaseColumns._ID}, " +
                    "${PersonEntries.TABLE_NAME}.${PersonEntries.COLUMN_NAME} " +
                    "FROM " +
                    "${ConversationEntries.Participants.TABLE_NAME} JOIN " +
                    "${PersonEntries.TABLE_NAME} ON " +
                    "${ConversationEntries.Participants.TABLE_NAME}." +
                    "${ConversationEntries.Participants.PERSON_ID} = " +
                    "${PersonEntries.TABLE_NAME}.${BaseColumns._ID} " +
                    "WHERE " +
                    "${ConversationEntries.Participants.TABLE_NAME}." +
                    "${ConversationEntries.Participants.CONVERSATION_ID} = ?",
            arrayOf("$idConversation")
        )

        val participants = HashSet<Person>()
        with(cursor) {
            if (moveToFirst()) {
                do {
                    val id = getId(this)
                    val name = getString(this, PersonEntries.COLUMN_NAME)
                    val person = Person(id, name)
                    Persons.addPersonIfNew(person)
                    participants.add(person)
                } while (moveToNext())
            }
            close()
        }
        return participants
    }

    /**
     * Persist a conversation box data.
     * = Save it
     */
    fun persist(conversationBoxData: ConversationBoxData): ConversationBoxData {
        Debug.i(TAG, "persist conversationBoxData : $conversationBoxData")
        // TODO persist other data
        for (conversationData in conversationBoxData.inbox!!)
            persist(conversationData)

        return conversationBoxData
    }

    /**
     * Persist a conversation data.
     * = Save it and give it an id
     */
    fun persist(conversationData: ConversationData): ConversationData {
        Debug.i(TAG, "persist conversationData : $conversationData")
        val values = ContentValues().apply {
            put(ConversationEntries.COLUMN_TITLE, conversationData.title)
            put(ConversationEntries.COLUMN_URI, conversationData.uri.toString())
            put(ConversationEntries.COLUMN_IS_STILL_PARTICIPANT, conversationData.isStillParticipant)
            put(ConversationEntries.COLUMN_MESSAGE_COUNT, conversationData.messageCount)
            put(ConversationEntries.COLUMN_PHOTO_COUNT, conversationData.photoCount)
            put(ConversationEntries.COLUMN_AUDIO_COUNT, conversationData.audioCount)
            put(ConversationEntries.COLUMN_GIF_COUNT, conversationData.gifCount)
            put(ConversationEntries.COLUMN_CREATION_DATE, conversationData.creationDate?.time)
        }

        val convId = writableDatabase?.insert(
            ConversationEntries.TABLE_NAME,
            null, values
        )

        conversationData.id = convId

        conversationData.participants?.let { persons ->
            for (person in persons) {
                when (val storedPerson = findPersonByName(person.name!!)) {
                    null -> persist(person)
                    else -> person.id = storedPerson.id
                }
                persistParticipant(conversationData, person)
            }
        }

        return conversationData
    }

    /**
     * Persist a participant into a conversation
     * = Save the Person if necessary and save the association
     */
    private fun persistParticipant(conversationData: ConversationData, person: Person) {
        Debug.i(TAG, "persistParticipant : $conversationData + $person")
        val values = ContentValues().apply {
            put(ConversationEntries.Participants.CONVERSATION_ID, conversationData.id)
            put(ConversationEntries.Participants.PERSON_ID, person.id)
        }

        writableDatabase?.insert(
            ConversationEntries.Participants.TABLE_NAME,
            null, values
        )
    }

    /**
     * Persist a person = Save it and give it an id
     */
    fun persist(person: Person): Person {
        Debug.i(TAG, "persist person : $person")
        val values = ContentValues().apply {
            put(PersonEntries.COLUMN_NAME, person.name)
        }

        val id = writableDatabase.insert(
            PersonEntries.TABLE_NAME,
            null,
            values
        )

        person.id = id
        Persons.addPerson(person)
        return person
    }

    /**
     * Find a person by its name
     *
     * @see Persons
     */
    fun findPersonByName(name: String): Person? {
        Persons.getPerson(name)?.let {
            return it
        }

        val projection = arrayOf(
            BaseColumns._ID,
            PersonEntries.COLUMN_NAME
        )

        val selection = "${PersonEntries.COLUMN_NAME} = ?"

        val cursor = readableDatabase.query(
            PersonEntries.TABLE_NAME,   // The table to query
            projection,             // The array of columns to return (pass null to get all)
            selection,              // The columns for the WHERE clause
            arrayOf(name),          // The values for the WHERE clause
            null,                   // don't group the rows
            null,                   // don't filter by row groups
            null               // The sort order
        )

        var person: Person? = null
        if (cursor.moveToFirst()) {
            val id = getId(cursor)
            val name = getString(cursor, PersonEntries.COLUMN_NAME)
            person = Person(id, name)

            Persons.addPerson(person)
        }

        cursor.close()
        return person
    }

    private fun getInt(cursor: Cursor, colName: String): Int {
        return cursor.getInt(cursor.getColumnIndexOrThrow(colName))
    }

    private fun getLong(cursor: Cursor, colName: String): Long {
        return cursor.getLong(cursor.getColumnIndexOrThrow(colName))
    }

    private fun getId(cursor: Cursor): Long {
        return cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID))
    }

    private fun getString(cursor: Cursor, colName: String): String {
        return cursor.getString(cursor.getColumnIndexOrThrow(colName))
    }

    private fun getBoolean(cursor: Cursor, colName: String): Boolean {
        return cursor.getInt(cursor.getColumnIndexOrThrow(colName)) != 0
    }

    private fun getDate(cursor: Cursor, colName: String): Date {
        return Date(cursor.getLong(cursor.getColumnIndexOrThrow(colName)))
    }
}