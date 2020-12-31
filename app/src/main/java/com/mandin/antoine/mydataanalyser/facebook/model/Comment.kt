package com.mandin.antoine.mydataanalyser.facebook.model

import java.util.*

data class Comment(
    val date: Date?,
    val content: String?,
    val group: String?,
    val where: String?,
    val medias: List<Media>?
)
