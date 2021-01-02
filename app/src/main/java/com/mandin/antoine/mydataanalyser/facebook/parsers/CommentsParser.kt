package com.mandin.antoine.mydataanalyser.facebook.parsers

import android.util.JsonReader
import com.mandin.antoine.mydataanalyser.facebook.model.Comment
import com.mandin.antoine.mydataanalyser.facebook.model.Media
import com.mandin.antoine.mydataanalyser.facebook.model.data.CommentsData
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class CommentsParser : FacebookParser<CommentsData>() {
    private val TAG = "CommentsParser"
    private val comments = ArrayList<Comment>()

    @Throws(IOException::class)
    override fun readWhole(reader: JsonReader): CommentsData {
        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                "comments" -> readCommentsArray(reader)
                else -> reader.skipValue()
            }
        }
        reader.endObject()
        return CommentsData(comments)
    }

    /**
     * Read the comments array
     */
    fun readCommentsArray(reader: JsonReader): ArrayList<Comment> {
        reader.beginArray()
        while (reader.hasNext()) {
            comments.add(readComment(reader))
        }
        reader.endArray()
        return comments
    }

    /**
     * Read a comment value
     */
    fun readComment(reader: JsonReader): Comment {
        var date: Date? = null
        var content: String? = null
        var group: String? = null
        var where: String? = null
        var medias: List<Media>? = null

        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                "timestamp" -> date = Date(reader.nextLong() * 1000)
                "data" -> {
                    val data = readData(reader)
                    content = data["comment"]
                    group = data["group"]
                }
                "title" -> where = readWhereFromTitle(reader)
                "attachments" -> medias = readAttachments(reader)//TODO read the attachements (check post parser)
                else -> reader.skipValue()
            }
        }
        reader.endObject()
        return Comment(date, content, group, where, medias)
    }

    /**
     * read "data" attribute : contains values such as the content and the group
     */
    fun readData(reader: JsonReader): Map<String, String> {
        val result = HashMap<String, String>()
        reader.beginArray()
        reader.beginObject()
        reader.nextName()//"comment":
        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                "comment" -> result["comment"] = nextString(reader)
                "group" -> result["group"] = nextString(reader)
                else -> reader.skipValue()
            }
        }
        reader.endObject()
        reader.endObject()
        reader.endArray()
        return result
    }

    /**
     * Read the title and extract the "where" value from it
     */
    fun readWhereFromTitle(reader: JsonReader): String {
        val title = nextString(reader)

        var value: String? = substringAfter(title, "a répondu au ")
        value?.let {
            return it.substring(0, it.length - 1)
        }

        value = substringAfter(title, "a commenté ")
        value?.let {
            return it.substring(0, it.length - 1)
        }

        value = substringAfter(title, "a répondu à ")
        value?.let {
            return it.substring(0, it.length - 1)
        }

        value = substringAfter(title, "a ajouté un commentaire sur ")
        value?.let {
            return it.substring(0, it.length - 1)
        }

        return title
    }


    /**
     * Read comments attachments (photos are handled only for now)
     */
    @Throws(IOException::class)
    fun readAttachments(reader: JsonReader): List<Media> {
        var result: List<Media> = emptyList()

        reader.beginArray()
        while (reader.hasNext()) {
            reader.beginObject()
            while (reader.hasNext()) {
                when (reader.nextName()) {
                    "data" -> {
                        result = readMedias(reader)
                    }
                    else -> {
                        reader.skipValue()
                    }
                }
            }
            reader.endObject()
        }
        reader.endArray()

        return result
    }

    /**
     * Read medias (photos only for now)
     */
    @Throws(IOException::class)
    fun readMedias(reader: JsonReader): List<Media> {
        val mediaList = ArrayList<Media>()

        reader.beginArray()
        while (reader.hasNext()) {
            readMedia(reader)?.let { mediaList.add(it) }
        }
        reader.endArray()

        return mediaList
    }

    /**
     * Read a media (photos only for now, else return null)
     */
    @Throws(IOException::class)
    fun readMedia(reader: JsonReader): Media? {
        var uri: String? = null
        var creationTimestamp: Date? = null

        reader.beginObject()
        reader.nextName() // Must be "media"
        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                "uri" -> {
                    uri = nextString(reader)
                }
                "creation_timestamp" -> {
                    creationTimestamp = Date(reader.nextLong())
                }
                else -> {
                    reader.skipValue()
                }
            }
        }
        reader.endObject()
        reader.endObject()

        if (uri == null || creationTimestamp == null) {
            //Not a media (may be a link or a sailing)
            return null
        }
        return Media(uri, creationTimestamp)
    }


}