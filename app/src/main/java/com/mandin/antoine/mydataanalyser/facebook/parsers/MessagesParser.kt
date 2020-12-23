package com.mandin.antoine.mydataanalyser.facebook.parsers

import android.util.JsonReader
import com.mandin.antoine.mydataanalyser.facebook.CharsetsUtils
import com.mandin.antoine.mydataanalyser.facebook.PhotoDates
import com.mandin.antoine.mydataanalyser.facebook.database.FacebookDbHelper
import com.mandin.antoine.mydataanalyser.facebook.model.Conversation
import com.mandin.antoine.mydataanalyser.facebook.model.Media
import com.mandin.antoine.mydataanalyser.facebook.model.Message
import com.mandin.antoine.mydataanalyser.facebook.model.Person
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

/**
 * Class used to parse a JSON file containing data about a facebook conversation
 *
 * File should be structured as :
 * ```
 * {
 * "participants" : [
 *   {
 *     "name": "..."
 *   },
 *   {
 *     "name": "?.?."
 *   },
 * ],
 * "messages": [
 *   {
 *     "sender_name": "L\u00c3\u00a9a Airiau",
 *     "timestamp_ms": 1607627040504,
 *     "content": "Oui par contre trop bien",
 *     "type": "Generic"
 *   },
 *   {
 *     "sender_name": "Vincent Biotteau",
 *     "timestamp_ms": 1607626879224,
 *     "content": "Benef plus d\u00e2\u0080\u0099attestation",
 *     "type": "Generic"
 *   }
 * ],
 * "title": "L\u00c3\u00a9g\u00c3\u00a9riens \u00f0\u009f\u008d\u0087",
 * "is_still_participant": true
 * }
 * ```
 */
class MessagesParser(private val dbHelper: FacebookDbHelper) {

    /**
     * `List` of persons participating into the conversation
     */
    private val participants = HashSet<Person>()

    /**
     * List of every sent messages
     */
    private val messages = ArrayList<Message>()

    private val medias = ArrayList<Media>()

    /**
     * Read a `JSON` object matching the pattern of a Conversation
     * @param input Input stream of the JSON file
     * @return the build Conversation
     * @throws IOException if an error occur while reading the file
     * @see Conversation
     */
    @Throws(IOException::class)
    fun readJson(input: InputStream): Conversation {
        val reader = JsonReader(InputStreamReader(input, Charsets.UTF_8))
        return reader.use {
            readConversation(reader)
        }
    }

    /**
     * Read a conversation into the `reader`
     * @param reader
     * @return Build conversation
     * @throws IOException if an error occur while reading the file
     * @see Conversation
     */
    @Throws(IOException::class)
    fun readConversation(reader: JsonReader): Conversation {
        var title: String? = null
        var isStillParticipant = false
        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                "participants" -> {
                    readParticipantsArray(reader)
                }
                "messages" -> {
                    readMessagesArray(reader)
                }
                "title" -> {
                    title = nextString(reader)
                }
                "is_still_participant" -> {
                    isStillParticipant = reader.nextBoolean()
                }
                else -> {
                    reader.skipValue()
                }
            }
        }
        reader.endObject()

        return Conversation(null, participants, messages, medias, title, isStillParticipant)
    }

    /**
     * Read the array of participants
     * @param reader
     * @return participant array
     * @throws IOException if an error occur while reading the file
     * @see Person
     */
    @Throws(IOException::class)
    fun readParticipantsArray(reader: JsonReader): HashSet<Person> {
        reader.beginArray()
        while (reader.hasNext()) {
            readParticipant(reader)?.let { participants.add(it) }
        }
        reader.endArray()

        return participants
    }

    /**
     * Read data about a participant
     * @param reader
     * @return The built Person object
     * @throws IOException if an error occur while reading the file
     * @see Person
     */
    @Throws(IOException::class)
    fun readParticipant(reader: JsonReader): Person? {
        var name: String? = null

        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                "name" -> {
                    name = nextString(reader)
                }
                else -> {
                    reader.skipValue()
                }
            }
        }
        reader.endObject()

        return getPerson(name)
    }

    /**
     * Read messages array
     * @param reader
     * @return The messages array
     * @throws IOException if an error occur while reading the file
     * @see Message
     */
    @Throws(IOException::class)
    fun readMessagesArray(reader: JsonReader): List<Message> {
        reader.beginArray()
        while (reader.hasNext()) {
            messages.add(readMessage(reader))
        }
        reader.endArray()

        return messages
    }

    /**
     * Read a message
     * @param reader
     * @return The built Message
     * @throws IOException if an error occur while reading the file
     * @see Message
     */
    @Throws(IOException::class)
    fun readMessage(reader: JsonReader): Message {
        var personName: String? = null
        var content: String? = null
        var date: Date? = null

        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                "sender_name" -> {
                    personName = nextString(reader)
                }
                "timestamp_ms" -> {
                    date = Date(reader.nextLong())
                }
                "content" -> {
                    content = nextString(reader)
                }
                "photos" -> {
                    content = "<<${readPhotosArray(reader)} photo(s)>>"
                }
                else -> {
                    reader.skipValue()
                }
            }
        }
        reader.endObject()

        val person = getPerson(personName)

        return Message(null, person, date, content)
    }

    @Throws(IOException::class)
    fun readPhotosArray(reader: JsonReader): Int {
        var count = 0
        reader.beginArray()
        while (reader.hasNext()) {
            medias.add(readPhoto(reader))
            count++
        }
        reader.endArray()

        return count
    }

    @Throws(IOException::class)
    fun readPhoto(reader: JsonReader): Media {
        var uri: String? = null
        var creationTimestamp: Long? = null

        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                "uri" -> {
                    uri = reader.nextString()
                }
                "creation_timestamp" -> {
                    creationTimestamp = reader.nextLong()
                }
            }
        }
        reader.endObject()

        if (uri != null && creationTimestamp != null) {
            PhotoDates.putFromUri(uri, creationTimestamp)
        }
        return Media(uri, creationTimestamp?.let { Date(it) })
    }

    private fun getPerson(name: String?): Person? {
        name?.let { name ->
            dbHelper.findPersonByName(name)?.let { person -> return person }
            return dbHelper.persist(Person(null, name))
        }
        return null
    }

    private fun nextString(reader: JsonReader): String {
        return CharsetsUtils.translateIsoToUtf(reader.nextString())
    }
}