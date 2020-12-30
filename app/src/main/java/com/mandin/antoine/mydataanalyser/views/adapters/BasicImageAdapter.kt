package com.mandin.antoine.mydataanalyser.views.adapters

import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.mandin.antoine.mydataanalyser.tools.ImageTaskLoader
import com.mandin.antoine.mydataanalyser.tools.TaskRunner
import com.mandin.antoine.mydataanalyser.utils.Debug

/**
 * Basic image adapter, showing a list of classic images (without date nor label..)
 */
class BasicImageAdapter<I : ImageAdapter.Image>(
    images: Array<I>,
    spanCount: Int = 3,
    imageClickListener: ImageClickListener? = null
) :
    ImageAdapter<I, BasicImageAdapter<I>.BasicViewHolder>(
        images, spanCount, imageClickListener
    ) {

    override fun createImageViewHolder(parent: ViewGroup, viewType: Int): BasicViewHolder {
        return BasicViewHolder(ImageView(parent.context))
    }

    /**
     * Basic image view holder : only display an image view
     */
    inner class BasicViewHolder(itemView: ImageView) :
        ImageAdapter<I, BasicViewHolder>.ImageViewHolder(itemView) {
        private var shownImage: Image? = null

        init {
            itemView.scaleType = ImageView.ScaleType.CENTER_CROP
            Debug.i("ImageAdapter", "holderSize = $imageSize")
            itemView.layoutParams = imageSize?.let {
                ViewGroup.LayoutParams(it, it)
            }
        }

        override fun showImage(image: I) {
            shownImage = image
            with(itemView as ImageView) {
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
        }

        override fun setImageClickListener(imageClickListener: ImageClickListener) {
            itemView.setOnClickListener {
                shownImage?.let { image -> imageClickListener.onImageClick(image) }
            }
        }
    }

}