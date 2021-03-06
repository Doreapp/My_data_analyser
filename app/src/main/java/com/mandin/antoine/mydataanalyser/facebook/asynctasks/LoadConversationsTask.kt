package com.mandin.antoine.mydataanalyser.facebook.asynctasks

import android.content.Context
import androidx.documentfile.provider.DocumentFile
import com.mandin.antoine.mydataanalyser.facebook.Paths
import com.mandin.antoine.mydataanalyser.facebook.database.FacebookDbHelper
import com.mandin.antoine.mydataanalyser.facebook.model.Conversation
import com.mandin.antoine.mydataanalyser.facebook.model.data.ConversationBoxData
import com.mandin.antoine.mydataanalyser.facebook.model.data.ConversationData
import com.mandin.antoine.mydataanalyser.facebook.parsers.MessagesParser
import com.mandin.antoine.mydataanalyser.tools.TaskObserver
import com.mandin.antoine.mydataanalyser.utils.Debug
import java.util.concurrent.Callable

/**
 * Async task to explore the facebook folder
 */
class LoadConversationsTask(
    private val docFile: DocumentFile,
    private val context: Context,
    private val observer: TaskObserver?
) : Callable<ConversationBoxData?> {
    private val TAG = "LoadConversationsTask"
    private val dbHelper = FacebookDbHelper(context)

    /**
     * Main function : Read files in the folder
     */
    override fun call(): ConversationBoxData? {
        Debug.i(TAG, "<call>")
        observer?.notify("Loading... [Clear database]")
        dbHelper.clear()
        observer?.notify("Loading... [Conversations]")
        val conversationBoxData = docFile.findFile(Paths.Messages.PATH)?.let {
            buildConversationBoxData(it)
        }
        dbHelper.close()
        return conversationBoxData
    }

    /**
     * Build conversation box data from the folder of conversations
     */
    fun buildConversationBoxData(messagesFolder: DocumentFile): ConversationBoxData {
        Debug.i(TAG, "buildConversationBoxData - messages folder'${messagesFolder.uri.path}'")
        val inboxConversations = ArrayList<ConversationData>()
        messagesFolder.findFile(Paths.Messages.INBOX)?.listFiles()?.let { folders ->
            var count = 0
            val length = folders.size
            // Reset progress bar
            observer?.setMaxProgress(length)
            for (folder in folders) {
                inboxConversations.add(buildConversationData(folder))

                count++
                observer?.notify("Analysing Conversations [inbox]. $count/$length conversations done.")
                observer?.notifyProgress(count)
            }
        }

        val conversationBoxData = ConversationBoxData(
            null,
            null,
            inboxConversations,
            null
        )
        Debug.i(
            TAG, "buildConversationBoxData - " +
                    "return $conversationBoxData"
        )
        return conversationBoxData
    }

    /**
     * Build a conversation data from the folder containing data about a conversation
     */
    fun buildConversationData(conversationFolder: DocumentFile): ConversationData {
        Debug.i(TAG, "buildConversationData")

        var photoCount = 0
        conversationFolder.findFile(Paths.Messages.Folder.PHOTOS)?.listFiles()?.let {
            photoCount = it.size
        }
        var gifCount = 0
        conversationFolder.findFile(Paths.Messages.Folder.GIFS)?.listFiles()?.let {
            gifCount = it.size
        }
        var audioCount = 0
        conversationFolder.findFile(Paths.Messages.Folder.AUDIOS)?.listFiles()?.let {
            audioCount = it.size
        }

        val parser = MessagesParser(dbHelper)
        var conversation: Conversation? = null
        for (child in conversationFolder.listFiles()) {
            if (child.isFile) {
                context.contentResolver.openInputStream(child.uri)?.let {
                    conversation = parser.readJson(it)
                }
            }
        }

        var messageCount = conversation?.messages?.size
        if (messageCount == null) messageCount = 0
        val conversationData = ConversationData(
            null,
            conversation?.title,
            uri = conversationFolder.uri,
            participants = conversation?.participants,
            conversation?.isStillParticipant,
            messageCount,
            photoCount,
            audioCount,
            gifCount,
            conversation?.findCreationDate()
        )

        Debug.i(
            TAG, "buildConversationData - " +
                    "return $conversationData (+its id)"
        )
        return dbHelper.persist(conversationData)
    }
}
