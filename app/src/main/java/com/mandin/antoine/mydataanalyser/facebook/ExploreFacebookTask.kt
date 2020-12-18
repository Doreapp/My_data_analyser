package com.mandin.antoine.mydataanalyser.facebook

import android.content.ContentResolver
import androidx.documentfile.provider.DocumentFile
import com.mandin.antoine.mydataanalyser.tools.TaskNotifier
import java.util.concurrent.Callable

class ExploreFacebookTask(
    private val docFile: DocumentFile,
    private val contentResolver: ContentResolver,
    private val notifier: TaskNotifier?
) : Callable<FacebookData?>, TaskNotifier {

    override fun call(): FacebookData {
        // Some long running task
        return FacebookData(docFile, contentResolver, this)
    }

    override fun notify(message: String) {
        notifier?.let {
            synchronized(it) {
                it.notify(message)
            }
        }
    }
}
