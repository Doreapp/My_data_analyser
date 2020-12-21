package com.mandin.antoine.mydataanalyser.views

import android.content.Context
import android.graphics.Bitmap
import android.util.DisplayMetrics
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.mandin.antoine.mydataanalyser.utils.Debug


class ImageAdapter<I : ImageAdapter.Image>(private var images: Array<I>, private val spanCount: Int = 3) :
    RecyclerView.Adapter<ImageAdapter<I>.ViewHolder>() {
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
        holder.showImage(images[position])
    }

    override fun getItemCount(): Int {
        return images.size
    }

    inner class ViewHolder(itemView: ImageView) : RecyclerView.ViewHolder(itemView) {

        init {
            itemView.scaleType = ImageView.ScaleType.CENTER_CROP
            Debug.i("ImageAdapter", "holderSize = $holderSize")
            itemView.layoutParams = holderSize?.let {
                ViewGroup.LayoutParams(it, it)
            }
        }

        fun showImage(image: Image) {
            with(itemView as ImageView) {
                setImageBitmap(image.getThumbnail(context))
            }
        }
    }

    interface Image {
        fun getThumbnail(context: Context): Bitmap?
        fun getPicture(context: Context): Bitmap?
    }
}