package com.mandin.antoine.mydataanalyser.facebook.asynctasks

import android.content.Context
import com.mandin.antoine.mydataanalyser.facebook.database.FacebookDbHelper
import com.mandin.antoine.mydataanalyser.facebook.model.data.ConversationBoxData
import com.mandin.antoine.mydataanalyser.tools.TaskObserver
import java.util.concurrent.Callable


/**
 * Async task used to read the database and retrieve facebook data
 */
class LoadDatabaseTask(
    context: Context,
    private val observer: TaskObserver?
) : Callable<ConversationBoxData?> {
    private val dbHelper = FacebookDbHelper(context)

    override fun call(): ConversationBoxData? {
        observer?.notify("Loading...")
        val conversationBoxData = dbHelper.findConversationBoxData(observer)
        dbHelper.close()
        conversationBoxData?.let {
            return ConversationBoxData(conversationBoxData)
        }
        return null
    }
}