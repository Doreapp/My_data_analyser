package com.mandin.antoine.mydataanalyser.facebook.database

import android.content.Context
import com.mandin.antoine.mydataanalyser.facebook.model.data.FacebookData
import com.mandin.antoine.mydataanalyser.tools.TaskObserver
import java.util.concurrent.Callable


/**
 * Async task used to read the database and retrieve facebook data
 */
class LoadDatabaseTask(
    context: Context,
    private val observer: TaskObserver?
) : Callable<FacebookData?> {
    private val dbHelper = FacebookDbHelper(context)

    override fun call(): FacebookData? {
        observer?.notify("Loading...")
        val conversationBoxData = dbHelper.findConversationBoxData()
        dbHelper.close()
        conversationBoxData?.let {
            return FacebookData(conversationBoxData)
        }
        return null
    }
}