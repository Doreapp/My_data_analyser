package com.mandin.antoine.mydataanalyser.facebook.model.data

import com.mandin.antoine.mydataanalyser.facebook.model.Post

/**
 * Posts data : list of posts
 */
data class PostsData(
    val posts: ArrayList<Post>
) {
    fun import(other: PostsData) {
        posts.addAll(other.posts)
    }
}
