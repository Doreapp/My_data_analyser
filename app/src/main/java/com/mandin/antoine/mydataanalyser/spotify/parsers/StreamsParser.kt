package com.mandin.antoine.mydataanalyser.spotify.parsers

import android.util.JsonReader
import com.mandin.antoine.mydataanalyser.spotify.model.Stream
import com.mandin.antoine.mydataanalyser.spotify.model.data.StreamsData
import com.mandin.antoine.mydataanalyser.tools.Parser
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class StreamsParser : Parser<StreamsData>() {
    private val streams = ArrayList<Stream>()
    private val dateReader = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH)

    override fun readWhole(reader: JsonReader): StreamsData {
        reader.beginArray()
        while (reader.hasNext()) {
            streams.add(readStream(reader))
        }
        reader.endArray()
        return StreamsData(streams)
    }

    private fun readStream(reader: JsonReader): Stream {
        var endTime: Date? = null
        var artistName: String? = null
        var trackName: String? = null
        var msPlayed: Long = 0

        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                "endTime" -> endTime = dateReader.parse(reader.nextString())
                "artistName" -> artistName = reader.nextString()
                "trackName" -> trackName = reader.nextString()
                "msPlayed" -> msPlayed = reader.nextLong()
            }
        }
        reader.endObject()
        return Stream(endTime, artistName, trackName, msPlayed)
    }
}