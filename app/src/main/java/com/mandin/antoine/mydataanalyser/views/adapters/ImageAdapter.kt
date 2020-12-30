package com.mandin.antoine.mydataanalyser.views.adapters

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * Class used to represent an adapter of images
 * [I] is the image type
 * [VH] is the view holder implementation
 */
abstract class ImageAdapter<
        I : ImageAdapter.Image,
        VH : ImageAdapter<I, VH>.ImageViewHolder>(
    private var images: Array<I>,
    private val spanCount: Int = 3,
    private val imageClickListener: ImageClickListener? = null
) : RecyclerView.Adapter<VH>() {
    protected var imageSize: Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        if (imageSize == null) {
            val displayMetrics = DisplayMetrics()
            parent.context.display?.getRealMetrics(displayMetrics)
            imageSize = displayMetrics.widthPixels / spanCount
        }
        val holder = createImageViewHolder(parent, viewType)
        imageClickListener?.let { holder.setImageClickListener(it) }
        return holder
    }

    /**
     * Create an image view holder
     */
    abstract fun createImageViewHolder(parent: ViewGroup, viewType: Int): VH

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.showImage(images[position])
    }

    override fun getItemCount(): Int {
        return images.size
    }

    /**
     * Inner abstract class used to specified needed methods for a view holder
     */
    abstract inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        /**
         * Show the image
         */
        abstract fun showImage(image: I)

        /**
         * Set a listener on click on the view holder's image
         */
        abstract fun setImageClickListener(imageClickListener: ImageClickListener)
    }

    /**
     * Image interface used to represent a container of an image
     */
    interface Image {
        fun getThumbnail(context: Context): Bitmap?
        fun getPicture(context: Context): Bitmap?
        fun getUri(context: Context): Uri?
    }

    /**
     * Listener of click on an image
     */
    interface ImageClickListener {
        fun onImageClick(image: Image)
        fun onImageClick(uri: Uri)
    }
}