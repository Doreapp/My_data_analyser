package com.mandin.antoine.mydataanalyser

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.GridLayoutManager
import com.mandin.antoine.mydataanalyser.utils.Constants
import com.mandin.antoine.mydataanalyser.utils.Debug
import com.mandin.antoine.mydataanalyser.views.ImageAdapter
import com.mandin.antoine.mydataanalyser.views.MarginDecoration
import kotlinx.android.synthetic.main.activity_gallery.*


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
                showImageList(children)
            }
        }
    }

    fun showImageList(array: Array<DocumentFile>) {
        Debug.i(TAG, "show image list (${array.size})")
        with(listImages) {
            setHasFixedSize(true)
            addItemDecoration(MarginDecoration())

            layoutManager = GridLayoutManager(applicationContext, 4)

            adapter = ImageAdapter(array, 4)
        }
    }
}