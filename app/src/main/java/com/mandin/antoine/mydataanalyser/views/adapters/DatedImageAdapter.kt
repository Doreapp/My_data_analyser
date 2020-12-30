package com.mandin.antoine.mydataanalyser.views.adapters

import android.graphics.Bitmap
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.mandin.antoine.mydataanalyser.R
import com.mandin.antoine.mydataanalyser.tools.ImageTaskLoader
import com.mandin.antoine.mydataanalyser.tools.TaskRunner
import com.mandin.antoine.mydataanalyser.utils.Debug
import kotlinx.android.synthetic.main.item_view_dated_image.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Adapter showing dated image views
 *
 * @see R.layout.item_view_dated_image
 */
class DatedImageAdapter<I : DatedImageAdapter.DatedImage>(
    images: Array<I>,
    spanCount: Int = 3,
    imageClickListener: ImageClickListener? = null
) : ImageAdapter<I, DatedImageAdapter<I>.DatedImageViewHolder>(
    images, spanCount, imageClickListener
) {
    private val dateFormatter = SimpleDateFormat("dd/MM/yy", Locale.ENGLISH)

    override fun createImageViewHolder(parent: ViewGroup, viewType: Int): DatedImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_view_dated_image, parent, false)
        return DatedImageViewHolder(view)
    }

    /**
     * Dated image view holder : display and image view and the date of the image
     *
     * @see R.layout.item_view_dated_image
     * @see DatedImage
     */
    inner class DatedImageViewHolder(itemView: View) :
        ImageAdapter<I, DatedImageViewHolder>.ImageViewHolder(itemView) {
        private var shownImage: Image? = null

        init {
            itemView.ivMain.scaleType = ImageView.ScaleType.CENTER_CROP
            Debug.i("ImageAdapter", "holderSize = $imageSize")
            itemView.ivMain.layoutParams = imageSize?.let {
                LinearLayout.LayoutParams(it, it)
            }
        }


        override fun showImage(image: I) {
            shownImage = image
            with(itemView.ivMain) {
                val uri: Uri? = image.getUri(context)
                if (uri != null) {
                    visibility = View.GONE
                    TaskRunner().executeAsync(
                        ImageTaskLoader(
                            context,
                            uri,
                            isThumbnail = true
                        ),
                        object : TaskRunner.Callback<Bitmap?> {
                            override fun onComplete(result: Bitmap?) {
                                visibility = View.VISIBLE
                                setImageBitmap(result)
                            }
                        }
                    )
                } else {
                    setImageBitmap(image.getThumbnail(context))
                }
            }
            image.getDate()?.let {
                itemView.tvDate.text = dateFormatter.format(it)
            }
        }

        override fun setImageClickListener(imageClickListener: ImageClickListener) {
            itemView.setOnClickListener {
                shownImage?.let { image -> imageClickListener.onImageClick(image) }
            }
        }

    }

    /**
     * Interface representing a dated image
     */
    interface DatedImage : Image {
        fun getDate(): Date?
    }

}