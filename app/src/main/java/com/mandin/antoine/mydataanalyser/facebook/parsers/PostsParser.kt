package com.mandin.antoine.mydataanalyser.facebook.parsers

import android.util.JsonReader
import com.mandin.antoine.mydataanalyser.facebook.model.Media
import com.mandin.antoine.mydataanalyser.facebook.model.Post
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class PostsParser : Parser<List<Post>>() {
    private val posts = ArrayList<Post>()

    @Throws(IOException::class)
    override fun readWhole(reader: JsonReader): List<Post> {
        reader.beginArray()
        while (reader.hasNext()) {
            posts.add(readPost(reader))
        }
        reader.endArray()

        return posts
    }

    @Throws(IOException::class)
    fun readPost(reader: JsonReader): Post {
        var content: String? = null
        var date: Date? = null
        var where: String? = null
        var medias = emptyList<Media>()

        reader.beginObject()

        while (reader.hasNext()) {
            when (reader.nextName()) {
                "timestamp" -> {
                    date = Date(reader.nextLong())
                }
                "data" -> {
                    content = readPostData(reader)
                }
                "title" -> {
                    where = readWhereFromTitle(reader)
                }
                "attachments" -> {
                    medias = readPostAttachments(reader)
                }
                else -> {
                    reader.skipValue()
                }
            }
        }

        return Post(content, date, where, medias)
    }

    @Throws(IOException::class)
    fun readPostData(reader: JsonReader): String? {
        var content: String? = null

        reader.beginArray()
        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                "post" -> {
                    content = nextString(reader)
                }
                else -> {
                    reader.skipValue()
                }
            }
        }
        reader.endObject()
        reader.endArray()

        return content
    }

    @Throws(IOException::class)
    fun readWhereFromTitle(reader: JsonReader): String? {
        val title = nextString(reader)

        var value = substringAfter(title, "a écrit sur le journal de ")
        value?.let { return it.substring(0, it.length - 1) }

        value = substringAfter(title, "a actualisé son statut")
        value?.let { return "<<Your Profile>>" }

        value = substringAfter(title, "a publié dans ")
        value?.let { return it.substring(0, it.length - 1) }

        value = substringAfter(title, "nouvelle photo dans le journal de ")
        value?.let { return it.substring(0, it.length - 1) }

        value = substringAfter(title, "nouvelles photos dans le journal de ")
        value?.let { return it.substring(0, it.length - 1) }

        return null
    }

    @Throws(IOException::class)
    fun readPostAttachments(reader: JsonReader): List<Media> {
        reader.beginArray()
        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                "data" -> {
                    return readMedias(reader)
                }
                else -> {
                    reader.skipValue()
                }
            }
        }

        return emptyList()
    }

    @Throws(IOException::class)
    fun readMedias(reader: JsonReader): List<Media> {
        val mediaList = ArrayList<Media>()

        reader.beginArray()
        while (reader.hasNext()) {
            mediaList.add(readMedia(reader))
        }
        reader.endArray()

        return mediaList
    }

    @Throws(IOException::class)
    fun readMedia(reader: JsonReader): Media {
        var uri: String? = null
        var creationTimestamp: Date? = null

        reader.beginObject()
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

        return Media(uri, creationTimestamp)
    }

    private fun substringAfter(str: String, subStr: String): String? {
        val index = str.indexOf(subStr)
        if (index >= 0)
            return str.substring(index + subStr.length)
        return null
    }
}