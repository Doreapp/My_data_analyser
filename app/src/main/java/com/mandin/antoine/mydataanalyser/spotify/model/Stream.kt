package com.mandin.antoine.mydataanalyser.spotify.model

import java.util.*

data class Stream(
    val date: Date?,
    val artist: String?,
    val trackName: String?,
    val duration: Long //Milliseconds
)

/*
"endTime" : "2019-12-08 20:35",
    "artistName" : "D.A.V",
    "trackName" : "ParoVie (feat. Damso)",
    "msPlayed" : 53307
 */