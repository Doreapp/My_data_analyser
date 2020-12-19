package com.mandin.antoine.mydataanalyser.facebook.database

import android.content.Context
import com.mandin.antoine.mydataanalyser.facebook.model.data.FacebookData
import com.mandin.antoine.mydataanalyser.tools.TaskNotifier
import java.util.concurrent.Callable


/**
 * Async task used to read the database and retrieve facebook data
 */
class LoadDatabaseTask(
    context: Context,
    private val notifier: TaskNotifier?
) : Callable<FacebookData?> {
    private val dbHelper = FacebookDbHelper(context)

    override fun call(): FacebookData? {
        notifier?.notify("Loading...")
        val conversationBoxData = dbHelper.findConversationBoxData()
        dbHelper.close()
        conversationBoxData?.let {
            return FacebookData(conversationBoxData)
        }
        return null
    }
}