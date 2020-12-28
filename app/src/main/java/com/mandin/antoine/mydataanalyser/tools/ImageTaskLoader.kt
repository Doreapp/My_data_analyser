package com.mandin.antoine.mydataanalyser.tools

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.mandin.antoine.mydataanalyser.utils.Constants
import com.mandin.antoine.mydataanalyser.utils.Debug
import java.util.concurrent.Callable

class ImageTaskLoader(
    private val context: Context,
    private val fileUri: Uri,
    private val followingPath: String? = null,
    private val isThumbnail: Boolean = true
) : Callable<Bitmap?> {


    override fun call(): Bitmap? {
        Debug.i("ImageTaskLoader", "call")
        val uri: Uri? = if (followingPath == null)
            fileUri else
            explore(DocumentFile.fromTreeUri(context, fileUri)!!)?.uri
        if (uri != null) {
            if (isThumbnail)
                context.contentResolver.openInputStream(uri)?.let {
                    val imageBitmap = BitmapFactory.decodeStream(it)
                    return ThumbnailUtils
                        .extractThumbnail(imageBitmap, Constants.THUMBNAIL_SIZE, Constants.THUMBNAIL_SIZE);
                }
            else
                context.contentResolver.openInputStream(uri)?.let {
                    BitmapFactory.decodeStream(it)
                }
        }
        return null
    }

    private fun explore(docFile: DocumentFile): DocumentFile? {
        var currentFile: DocumentFile? = docFile
        for (fileName in followingPath!!.split("/")) {
            currentFile = currentFile?.findFile(fileName)
        }
        return currentFile
    }
}