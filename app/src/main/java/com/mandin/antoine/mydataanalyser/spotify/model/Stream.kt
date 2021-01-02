package com.mandin.antoine.mydataanalyser.spotify.model

import java.util.*

/**
 * Spotify Stream data class
 */
data class Stream(
    val date: Date?,
    val artist: String?,
    val trackName: String?,
    val duration: Long //Milliseconds
)