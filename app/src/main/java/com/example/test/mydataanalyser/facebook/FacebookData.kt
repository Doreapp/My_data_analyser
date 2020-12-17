package com.example.test.mydataanalyser.facebook

import androidx.documentfile.provider.DocumentFile

class FacebookData(val rootFolder: DocumentFile) {
    val TAG = "FacebookData"
    val messagesFolder = rootFolder.findFile(Paths.PATH_MESSAGES)
    val messagesData = MessagesData()

    object Paths {
        const val PATH_ABOUT_YOU = "about_you"

        // TODO Add other paths (C:\Users\antoi\Downloads\tmp\facebook-AntoineMandin)
        const val PATH_MESSAGES = "messages"

        const val PATH_MESSAGES_ARCHIVED_THREADS = "archived_threads"
        const val PATH_MESSAGES_FILTERED_THREADS = "filtered_threads"
        const val PATH_MESSAGES_INBOX = "inbox"
        const val PATH_MESSAGES_MESSAGE_REQUESTS = "message_requests"
        const val PATH_MESSAGES_STICKERS_USED = "stickers_used"
    }


    inner class MessagesData {
        var archivedThreadFolder = messagesFolder?.findFile(Paths.PATH_MESSAGES_ARCHIVED_THREADS)
        var filteredThreadFolder = messagesFolder?.findFile(Paths.PATH_MESSAGES_FILTERED_THREADS)
        var inboxFolder = messagesFolder?.findFile(Paths.PATH_MESSAGES_INBOX)
        var messageRequestsFolder = messagesFolder?.findFile(Paths.PATH_MESSAGES_MESSAGE_REQUESTS)
        var stickersUsedFolder = messagesFolder?.findFile(Paths.PATH_MESSAGES_STICKERS_USED)

        val archivedThreadCount: Int?
            get() = archivedThreadFolder?.listFiles()?.size

        val filteredThreadCount: Int?
            get() = filteredThreadFolder?.listFiles()?.size

        val inboxCount: Int?
            get() = inboxFolder?.listFiles()?.size

        val messageRequestsCount: Int?
            get() = messageRequestsFolder?.listFiles()?.size

        val stickersUsedCount: Int?
            get() = stickersUsedFolder?.listFiles()?.size

        fun counts(): String {
            return "MessagesData{" +
                    "archivedThreadCount:$archivedThreadCount, " +
                    "filteredThreadCount:$filteredThreadCount, " +
                    "inboxCount:$inboxCount, " +
                    "messageRequestsCount:$messageRequestsCount, " +
                    "stickersUsedCount:$stickersUsedCount}"
        }
    }
}