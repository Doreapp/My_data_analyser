package com.mandin.antoine.mydataanalyser.facebook;

import android.content.ContentResolver
import androidx.documentfile.provider.DocumentFile;
import com.mandin.antoine.mydataanalyser.utils.Debug

class MessagesData(messagesFolder: DocumentFile?, contentResolver: ContentResolver) {
    var archivedThreadFolder = messagesFolder?.findFile(FacebookData.Paths.PATH_MESSAGES_ARCHIVED_THREADS)
    var filteredThreadFolder = messagesFolder?.findFile(FacebookData.Paths.PATH_MESSAGES_FILTERED_THREADS)
    var inboxFolder = messagesFolder?.findFile(FacebookData.Paths.PATH_MESSAGES_INBOX)
    var messageRequestsFolder = messagesFolder?.findFile(FacebookData.Paths.PATH_MESSAGES_MESSAGE_REQUESTS)
    var stickersUsedFolder = messagesFolder?.findFile(FacebookData.Paths.PATH_MESSAGES_STICKERS_USED)
    val conversations = ArrayList<ConversationData>()

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

    init {
        Debug.i("MessagesData", "<init>")
        val startTime = System.currentTimeMillis()
        inboxFolder?.listFiles()?.let { folders ->
            for (folder in folders)
                conversations.add(ConversationData(folder, contentResolver))
        }
        Debug.i("MessagesData", "resolving message took : ${System.currentTimeMillis() - startTime}ms")
    }

    val totalMessageCount: Int
        get() {
            var totalMessageCount = 0
            for (conv in conversations)
                totalMessageCount += conv.totalMessageCount
            return totalMessageCount
        }

    fun counts(): String {
        return "MessagesData{" +
                "archivedThreadCount:$archivedThreadCount, " +
                "filteredThreadCount:$filteredThreadCount, " +
                "inboxCount:$inboxCount, " +
                "messageRequestsCount:$messageRequestsCount, " +
                "stickersUsedCount:$stickersUsedCount}"
    }
}


