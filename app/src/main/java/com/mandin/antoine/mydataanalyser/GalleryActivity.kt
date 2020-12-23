package com.mandin.antoine.mydataanalyser

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.GridLayoutManager
import com.mandin.antoine.mydataanalyser.facebook.PhotoDates
import com.mandin.antoine.mydataanalyser.facebook.asynctasks.LoadConversationPhotosTask
import com.mandin.antoine.mydataanalyser.tools.TaskRunner
import com.mandin.antoine.mydataanalyser.utils.Constants
import com.mandin.antoine.mydataanalyser.utils.Debug
import com.mandin.antoine.mydataanalyser.views.ImageAdapter
import com.mandin.antoine.mydataanalyser.views.LoadingDialog
import com.mandin.antoine.mydataanalyser.views.MarginDecoration
import kotlinx.android.synthetic.main.activity_gallery.*
import java.util.*

/**
 * Activity for display a set of photos
 */
class GalleryActivity : AppCompatActivity() {
    private val TAG = "GalleryActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        readIntent()

        ivFullscreen.setOnClickListener {
            it.visibility = View.GONE
        }
    }

    /**
     * Read the activity's launching intent
     */
    private fun readIntent() {
        Debug.i(TAG, "read Intent")
        intent.extras?.getString(Constants.EXTRA_PHOTO_FOLDER_URI)?.let { uriDesc ->
            val uri = Uri.parse(uriDesc)
            readPhotosAsync(uri)
        }
    }

    /**
     * Launch the reading of photos in an async task
     *
     * @param uri the uri of the photo folder to read and display
     * @see LoadConversationPhotosTask
     */
    private fun readPhotosAsync(uri: Uri) {
        with(LoadingDialog(this)) {
            hasProgress = true
            TaskRunner().executeAsync(
                LoadConversationPhotosTask(this@GalleryActivity, uri, observer),
                object : TaskRunner.Callback<Array<DatedImage>> {
                    override fun onComplete(result: Array<DatedImage>) {
                        showImageList(result)
                        dismiss()
                    }
                })

            show()
        }
    }

    /**
     * Display an image list
     *
     * @param array the array of [DatedImage] to display
     */
    fun showImageList(array: Array<DatedImage>) {
        Debug.i(TAG, "show image list (${array.size})")
        with(listImages) {
            setHasFixedSize(true)
            addItemDecoration(MarginDecoration())

            layoutManager = GridLayoutManager(applicationContext, 4)

            adapter = ImageAdapter(array, 4,
                object : ImageAdapter.ImageClickListener {
                    override fun onImageClick(image: ImageAdapter.Image) {
                        displayImageFullScreen(image)
                    }
                })
        }
    }

    fun displayImageFullScreen(image: ImageAdapter.Image) {
        with(ivFullscreen) {
            visibility = View.VISIBLE
            setImageBitmap(image.getPicture(context))
        }
    }


    /**
     * Class representing a dated image
     */
    class DatedImage(private val file: DocumentFile) : ImageAdapter.Image {
        val date = file.name?.let { PhotoDates.getFromName(it) }
        var thumbnail: Bitmap? = null

        /**
         * Return a thumbnail
         *
         * @see Constants.THUMBNAIL_SIZE
         */
        override fun getThumbnail(context: Context): Bitmap? {
            if (thumbnail == null)
                context.contentResolver.openInputStream(file.uri)?.let {
                    val imageBitmap = BitmapFactory.decodeStream(it)
                    thumbnail = ThumbnailUtils
                        .extractThumbnail(imageBitmap, Constants.THUMBNAIL_SIZE, Constants.THUMBNAIL_SIZE);
                }
            return thumbnail
        }

        /**
         * Return the whole picture
         */
        override fun getPicture(context: Context): Bitmap? {
            return context.contentResolver.openInputStream(file.uri)?.let {
                BitmapFactory.decodeStream(it)
            }
        }

    }
}