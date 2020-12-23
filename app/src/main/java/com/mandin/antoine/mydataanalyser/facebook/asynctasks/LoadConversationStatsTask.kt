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
import java.util.concurrent.Callable

class LoadConversationStatsTask(
    private val context: Context,
    private val dbHelper: FacebookDbHelper,
    private val conversationId: Long,
    private val observer: TaskObserver?
) : Callable<LoadConversationStatsTask.Result?> {
    private val findConversationProgressCost = 5
    private var result = Result()

    override fun call(): LoadConversationStatsTask.Result? {
        observer?.notify("Searching conversation...")
        dbHelper.findConversationById(conversationId)?.let {
            result.conversationData = it
            findConversation(it)

            result.conversation?.let { conversation ->
                result.conversationStats = ConversationStats(conversation)
            }
        }
        return result
    }

    // TODO progress
    private fun findConversation(conversationData: ConversationData) {
        conversationData.uri?.let { uri ->
            DocumentFile.fromTreeUri(context, uri)?.let { folder ->
                val parser = MessagesParser(dbHelper)
                for (child in folder.listFiles()) {
                    if (child.isFile) {
                        context.contentResolver.openInputStream(child.uri)?.let {
                            result.conversation = parser.readJson(it)
                        }
                    }
                }

                folder.findFile(Paths.Messages.Folder.PHOTOS)?.let { photosFolder ->
                    result.photoFolderUri = photosFolder.uri
                }
            }
        }
    }

    inner class Result(
        var conversationData: ConversationData? = null,
        var conversation: Conversation? = null,
        var conversationStats: ConversationStats? = null,
        var photoFolderUri: Uri? = null
    )
}