package com.mandin.antoine.mydataanalyser.facebook.model

import java.util.*

/**
 * A facebook post
 */
data class Post(
    val content: String?,
    val date: Date?,
    val where: String?,
    val medias: List<Media>
)
