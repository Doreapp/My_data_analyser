package com.mandin.antoine.mydataanalyser.facebook

import android.content.ContentResolver
import androidx.documentfile.provider.DocumentFile
import com.mandin.antoine.mydataanalyser.facebook.model.Conversation
import com.mandin.antoine.mydataanalyser.facebook.model.Person
import com.mandin.antoine.mydataanalyser.utils.Debug
import java.util.*
import kotlin.collections.HashMap

class ConversationData(conversationFolder: DocumentFile, contentResolver: ContentResolver) {
    var title: String? = null
    var participants: HashSet<Person>? = null
    var photoCount: Int = 0
    var firstMessageDate: Date? = null
    val messageCount = HashMap<Person, Int>()
    var totalMessageCount: Int = 0

    init {
        val startTime = System.currentTimeMillis()
        conversationFolder.findFile(FacebookData.Paths.PATH_MESSAGES_PHOTOS)?.listFiles()?.let {
            photoCount = it.size
        }
        val parser = MessagesParser()
        var conversation: Conversation? = null
        for (child in conversationFolder.listFiles()) {
            if (child.isFile) {
                contentResolver.openInputStream(child.uri)?.let { conversation = parser.readJson(it) }
            }
        }
        title = conversation?.title
        participants = conversation?.participants
        conversation?.messages?.let { messages ->
            for (message in messages) {
                message.person?.let { participant ->
                    when (val count = messageCount[message.person]) {
                        null -> messageCount[participant] = 0
                        else -> messageCount[participant] = count + 1
                    }
                }

                message.sendingDate?.let { date ->
                    if (firstMessageDate == null || firstMessageDate!!.after(date)) {
                        firstMessageDate = date
                    }
                }

                totalMessageCount++
            }
        }
        Debug.i(
            "ConversationData", "read of conv '$title' finished in " +
                    "${System.currentTimeMillis() - startTime}ms (contains $totalMessageCount messages)" +
                    " started in $firstMessageDate"
        )
    }

    override fun toString(): String {
        return "ConversationData(title=$title, " +
                "participants=$participants, " +
                "photoCount=$photoCount, " +
                "firstMessageDate=$firstMessageDate, \n" +
                "messageCount=${messageCount.toList().toTypedArray().contentToString()},\n" +
                "totalMessageCount=$totalMessageCount)"
    }


}