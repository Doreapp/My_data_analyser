package com.example.test.mydataanalyser.facebook

import android.content.ContentResolver
import androidx.documentfile.provider.DocumentFile

class FacebookData(rootFolder: DocumentFile, contentResolver: ContentResolver) {
    val TAG = "FacebookData"
    private val messagesFolder = rootFolder.findFile(Paths.PATH_MESSAGES)
    val messagesData = MessagesData(messagesFolder, contentResolver)

    object Paths {
        const val PATH_ABOUT_YOU = "about_you"

        // TODO Add other paths (C:\Users\antoi\Downloads\tmp\facebook-AntoineMandin)
        const val PATH_MESSAGES = "messages"

        const val PATH_MESSAGES_ARCHIVED_THREADS = "archived_threads"
        const val PATH_MESSAGES_FILTERED_THREADS = "filtered_threads"
        const val PATH_MESSAGES_INBOX = "inbox"
        const val PATH_MESSAGES_MESSAGE_REQUESTS = "message_requests"
        const val PATH_MESSAGES_STICKERS_USED = "stickers_used"

        const val PATH_MESSAGES_PHOTOS = "photos"
    }



}