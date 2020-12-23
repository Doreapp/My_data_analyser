package com.mandin.antoine.mydataanalyser

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.GridLayoutManager
import com.mandin.antoine.mydataanalyser.facebook.PhotoDates
import com.mandin.antoine.mydataanalyser.utils.Constants
import com.mandin.antoine.mydataanalyser.utils.Debug
import com.mandin.antoine.mydataanalyser.views.ImageAdapter
import com.mandin.antoine.mydataanalyser.views.MarginDecoration
import kotlinx.android.synthetic.main.activity_gallery.*
import java.util.*


class GalleryActivity : AppCompatActivity() {
    private val TAG = "GalleryActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        readIntent()
    }

    private fun readIntent() {
        Debug.i(TAG, "read Intent")
        intent.extras?.getString(Constants.EXTRA_PHOTO_FOLDER_URI)?.let { uriDesc ->
            val uri = Uri.parse(uriDesc)
            DocumentFile.fromTreeUri(this, uri)?.listFiles()?.let { children ->
                showImageList(buildDatedImageList(children))
            }
        }
    }

    private fun buildDatedImageList(array: Array<DocumentFile>): Array<DatedImage> {
        val set = TreeSet<DatedImage>(kotlin.Comparator { o1, o2 ->
            if (o1.date != null && o2.date != null)
                o1.date.compareTo(o2.date)
            else
                0
        })
        for (file in array) {
            set.add(DatedImage(file))
        }
        return set.toTypedArray()
    }

    fun showImageList(array: Array<DatedImage>) {
        Debug.i(TAG, "show image list (${array.size})")
        with(listImages) {
            setHasFixedSize(true)
            addItemDecoration(MarginDecoration())

            layoutManager = GridLayoutManager(applicationContext, 4)

            adapter = ImageAdapter(array, 4)
        }
    }

    inner class DatedImage(private val file: DocumentFile) : ImageAdapter.Image {
        val date = file.name?.let { PhotoDates.getFromName(it) }
        var thumbnail: Bitmap? = null

        override fun getThumbnail(context: Context): Bitmap? {
            if (thumbnail == null)
                context.contentResolver.openInputStream(file.uri)?.let {
                    val imageBitmap = BitmapFactory.decodeStream(it)
                    thumbnail = ThumbnailUtils
                        .extractThumbnail(imageBitmap, Constants.THUMBNAIL_SIZE, Constants.THUMBNAIL_SIZE);
                }
            return thumbnail
        }

        override fun getPicture(context: Context): Bitmap? {
            return context.contentResolver.openInputStream(file.uri)?.let {
                BitmapFactory.decodeStream(it)
            }
        }

    }
}