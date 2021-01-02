package com.mandin.antoine.mydataanalyser.spotify.model.data

import com.mandin.antoine.mydataanalyser.spotify.model.Stream

/**
 * Data about streams, contains an array of [Stream]
 */
data class StreamsData(
    val streams: ArrayList<Stream>
) {
    fun import(other: StreamsData) {
        streams.addAll(other.streams)
    }
}
