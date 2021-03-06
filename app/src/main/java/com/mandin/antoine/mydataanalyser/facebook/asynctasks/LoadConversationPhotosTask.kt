package com.mandin.antoine.mydataanalyser.facebook.asynctasks

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.mandin.antoine.mydataanalyser.GalleryActivity
import com.mandin.antoine.mydataanalyser.tools.TaskObserver
import java.util.*
import java.util.concurrent.Callable

/**
 * Async task for loading photos from a folder.
 *
 * Browse a folder (specified with [uri]) and sort contained photos as [GalleryActivity.DatedImage]
 */
class LoadConversationPhotosTask(
    private val context: Context,
    private val uri: Uri,
    private val observer: TaskObserver? = null
) : Callable<Array<GalleryActivity.DatedImage>> {


    override fun call(): Array<GalleryActivity.DatedImage> {
        observer?.notify("Opening photos folder...")
        val set = TreeSet<GalleryActivity.DatedImage> { o1, o2 ->
            if (o1.getDate() != null && o2.getDate() != null)
                o1.getDate()!!.compareTo(o2.getDate())
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