package com.example.test.mydataanalyser.facebook

import android.content.ContentResolver
import androidx.documentfile.provider.DocumentFile
import java.util.concurrent.Callable

class ExploreFacebookTask(private val docFile: DocumentFile, private val contentResolver: ContentResolver) :
    Callable<FacebookData?> {
    override fun call(): FacebookData {
        // Some long running task
        return FacebookData(docFile, contentResolver)
    }
}
