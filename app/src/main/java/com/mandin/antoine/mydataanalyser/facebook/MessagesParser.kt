package com.mandin.antoine.mydataanalyser.facebook

import android.util.JsonReader
import com.mandin.antoine.mydataanalyser.facebook.model.Conversation
import com.mandin.antoine.mydataanalyser.facebook.model.Message
import com.mandin.antoine.mydataanalyser.facebook.model.Person
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
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
class MessagesParser {

    /**
     * `List` of persons participating into the conversation
     */
    private val participants = HashSet<Person>()

    /**
     * List of every sent messages
     */
    private val messages = ArrayList<Message>()

    /**
     * Read a `JSON` object matching the pattern of a Conversation
     * @param input Input stream of the JSON file
     * @return the build Conversation
     * @throws IOException if an error occur while reading the file
     * @see Conversation
     */
    @Throws(IOException::class)
    fun readJson(input: InputStream): Conversation {
        val reader = JsonReader(InputStreamReader(input, StandardCharsets.ISO_8859_1))
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
        var isStillParticipant: Boolean = false
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
                    title = reader.nextString()
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

        return Conversation(participants, messages, title, isStillParticipant)
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
                    name = reader.nextString()
                }
                else -> {
                    reader.skipValue()
                }
            }
        }
        reader.endObject()
        name?.let { return Persons.getPerson(name) }
        return null
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
                    personName = reader.nextString()
                }
                "timestamp_ms" -> {
                    date = Date(reader.nextLong())
                }
                "content" -> {
                    content = reader.nextString()
                }
                else -> {
                    reader.skipValue()
                }
            }
        }
        reader.endObject()

        val person = personName?.let { Persons.getPerson(personName) }

        return Message(person, date, content)
    }
}