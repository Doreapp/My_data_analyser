package com.mandin.antoine.mydataanalyser.facebook.model.data

import com.mandin.antoine.mydataanalyser.facebook.model.Comment

/**
 * Data about comments
 */
data class CommentsData(
    val comments: ArrayList<Comment>
) {
    fun import(other: CommentsData) {
        comments.addAll(other.comments)
    }
}