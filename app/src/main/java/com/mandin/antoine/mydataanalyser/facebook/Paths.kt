package com.mandin.antoine.mydataanalyser.facebook

/**
 * Paths in the facebook folder
 */
object Paths {
    const val PATH_ABOUT_YOU = "about_you"

    // TODO Add other paths (C:\Users\antoi\Downloads\tmp\facebook-AntoineMandin)

    /**
     * Paths to messages folder and sub folders
     */
    object Messages {
        const val PATH = "messages"
        const val ARCHIVED_THREADS = "archived_threads"
        const val FILTERED_THREADS = "filtered_threads"
        const val INBOX = "inbox"
        const val MESSAGE_REQUESTS = "message_requests"
        const val STICKERS_USED = "stickers_used"

        object Folder {
            const val PHOTOS = "photos"
            const val AUDIOS = "audio"
            const val GIFS = "gifs"
        }
    }

    object Posts {
        const val PATH = "posts"
        const val YOUR_POSTS_X = "your_posts_"
    }
}