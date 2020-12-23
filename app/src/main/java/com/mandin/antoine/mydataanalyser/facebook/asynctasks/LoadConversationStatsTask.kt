package com.mandin.antoine.mydataanalyser.facebook.asynctasks

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.mandin.antoine.mydataanalyser.facebook.Paths
import com.mandin.antoine.mydataanalyser.facebook.database.FacebookDbHelper
import com.mandin.antoine.mydataanalyser.facebook.model.Conversation
import com.mandin.antoine.mydataanalyser.facebook.model.data.ConversationData
import com.mandin.antoine.mydataanalyser.facebook.model.data.ConversationStats
import com.mandin.antoine.mydataanalyser.facebook.parsers.MessagesParser
import com.mandin.antoine.mydataanalyser.tools.TaskObserver
import com.mandin.antoine.mydataanalyser.utils.Debug
import java.util.concurrent.Callable

/**
 * Async task for loading a conversation and its statistics
 */
class LoadConversationStatsTask(
    private val context: Context,
    private val dbHelper: FacebookDbHelper,
    private val conversationId: Long,
    private val observer: TaskObserver?
) : Callable<LoadConversationStatsTask.Result> {
    private var result = Result()

    override fun call(): LoadConversationStatsTask.Result {
        Debug.i(TAG, "call()")
        observer?.setMaxProgress(findStatsCost + listMessagesCost + searchConversationCost)
        observer?.notify("Searching for conversation...")
        dbHelper.findConversationById(conversationId)?.let {
            observer?.notifyProgress(searchConversationCost)
            result.conversationData = it
            readConversation(it)

            result.conversation?.let { conversation ->
                observer?.notify("Building statistics...")
                result.conversationStats = ConversationStats(conversation)
                observer?.notifyProgress(searchConversationCost + listMessagesCost + findStatsCost)
            }
        }
        return result
    }

    /**
     * Read a conversation : browse messages and build stats
     * @see Conversation
     * @see ConversationStats
     * @see MessagesParser
     */
    private fun readConversation(conversationData: ConversationData) {
        Debug.i(TAG, "findConversation() conversationData=$conversationData")
        observer?.notify("Listing messages...")
        conversationData.uri?.let { uri ->
            DocumentFile.fromTreeUri(context, uri)?.let { folder ->
                val parser = MessagesParser(dbHelper)
                val children = folder.listFiles()
                var childrenDone = 0
                val childrenCount = children.size
                for (child in children) {
                    if (child.isFile) {
                        context.contentResolver.openInputStream(child.uri)?.let {
                            result.conversation = parser.readJson(it)
                        }
                    }
                    childrenDone++
                    observer?.notifyProgress(
                        searchConversationCost +
                                listMessagesCost * childrenDone / childrenCount
                    )
                }

                folder.findFile(Paths.Messages.Folder.PHOTOS)?.let { photosFolder ->
                    result.photoFolderUri = photosFolder.uri
                }
                observer?.notifyProgress(searchConversationCost + listMessagesCost)
            }
        }
    }

    /**
     * Result of this task
     */
    inner class Result(
        var conversationData: ConversationData? = null,
        var conversation: Conversation? = null,
        var conversationStats: ConversationStats? = null,
        var photoFolderUri: Uri? = null
    )

    companion object {
        const val TAG = "LoadConversationStatsTask"

        const val searchConversationCost = 5
        const val listMessagesCost = 85
        const val findStatsCost = 10
    }
}