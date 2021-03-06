package com.mandin.antoine.mydataanalyser.facebook.parsers

import android.util.JsonReader
import com.mandin.antoine.mydataanalyser.facebook.model.Media
import com.mandin.antoine.mydataanalyser.facebook.model.Post
import com.mandin.antoine.mydataanalyser.facebook.model.data.PostsData
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

/**
 * FacebookParser for facebook posts
 */
class PostsParser : FacebookParser<PostsData>() {
    private val TAG = "PostsParser"
    private val posts = ArrayList<Post>()

    /**
     * Read the whole array of posts
     */
    @Throws(IOException::class)
    override fun readWhole(reader: JsonReader): PostsData {
        reader.beginArray()
        while (reader.hasNext()) {
            posts.add(readPost(reader))
        }
        reader.endArray()

        return PostsData(posts)
    }

    /**
     * Read a post
     */
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
                    date = Date(reader.nextLong() * 1000L)
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
        reader.endObject()

        return Post(content, date, where, medias)
    }

    /**
     * Read post data (content)
     */
    @Throws(IOException::class)
    fun readPostData(reader: JsonReader): String? {
        var content: String? = null

        reader.beginArray()
        while (reader.hasNext()) {
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
        }
        reader.endArray()

        return content
    }

    /**
     * Read title and extract information "where" (the post was posted) from it
     */
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

    /**
     * Read post attachments (photos are handled only for now)
     */
    @Throws(IOException::class)
    fun readPostAttachments(reader: JsonReader): List<Media> {
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