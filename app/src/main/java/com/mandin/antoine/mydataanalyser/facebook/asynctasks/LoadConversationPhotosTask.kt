package com.mandin.antoine.mydataanalyser.facebook.asynctasks

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.mandin.antoine.mydataanalyser.GalleryActivity
import com.mandin.antoine.mydataanalyser.tools.TaskObserver
import java.util.*
import java.util.concurrent.Callable

class LoadConversationPhotosTask(
    private val context: Context,
    private val uri: Uri,
    private val observer: TaskObserver? = null
) : Callable<Array<GalleryActivity.DatedImage>> {


    override fun call(): Array<GalleryActivity.DatedImage> {
        observer?.notify("Opening photos folder...")
        val set = TreeSet<GalleryActivity.DatedImage> { o1, o2 ->
            if (o1.date != null && o2.date != null)
                o1.date.compareTo(o2.date)
            else
                0
        }

        DocumentFile.fromTreeUri(context, uri)?.listFiles()?.let { files ->
            observer?.notify("Loading photos...")

            observer?.setMaxProgress(files.size)
            var progress = 0
            for (file in files) {
                set.add(GalleryActivity.DatedImage(file))
                progress++
                observer?.notifyProgress(progress)
            }
        }
        return set.toTypedArray()
    }

}