package com.mandin.antoine.mydataanalyser.views

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.media.ThumbnailUtils
import android.util.DisplayMetrics
import android.view.ViewGroup
import android.widget.ImageView
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.RecyclerView
import com.mandin.antoine.mydataanalyser.utils.Constants
import com.mandin.antoine.mydataanalyser.utils.Debug


class ImageAdapter(private var files: Array<DocumentFile>, private val spanCount: Int = 3) :
    RecyclerView.Adapter<ImageAdapter.ViewHolder>() {
    private var holderSize: Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (holderSize == null) {
            val displayMetrics = DisplayMetrics()
            parent.context.display?.getRealMetrics(displayMetrics)
            holderSize = displayMetrics.widthPixels / spanCount
        }
        val view = ImageView(parent.context)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.showImageFromFile(file = files[position])
    }

    override fun getItemCount(): Int {
        return files.size
    }

    inner class ViewHolder(itemView: ImageView) : RecyclerView.ViewHolder(itemView) {

        init {
            itemView.scaleType = ImageView.ScaleType.CENTER_CROP
            Debug.i("ImageAdapter", "holderSize = $holderSize")
            itemView.layoutParams = holderSize?.let {
                ViewGroup.LayoutParams(it, it)
            }
        }

        fun showImageFromFile(file: DocumentFile) {
            with(itemView as ImageView) {
                //First recycle
                drawable.let {
                    if (it is BitmapDrawable) {
                        it.bitmap.recycle()
                    }
                }

                //Then set the new drawable
                itemView.context.contentResolver.openInputStream(file.uri)?.let {
                    val imageBitmap = BitmapFactory.decodeStream(it)
                    val thumbnailBitmap = ThumbnailUtils
                        .extractThumbnail(imageBitmap, Constants.THUMBNAIL_SIZE, Constants.THUMBNAIL_SIZE);
                    setImageBitmap(thumbnailBitmap)
                }
            }
        }
    }
}