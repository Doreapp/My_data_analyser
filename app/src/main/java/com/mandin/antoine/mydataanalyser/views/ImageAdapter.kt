package com.mandin.antoine.mydataanalyser.views

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.view.ViewGroup
import android.widget.ImageView
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.RecyclerView

class ImageAdapter(private var files: Array<DocumentFile>) : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ImageView(parent.context))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.showImageFromFile(file = files[position])
    }

    override fun getItemCount(): Int {
        return files.size
    }

    inner class ViewHolder(itemView: ImageView) : RecyclerView.ViewHolder(itemView) {

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
                    setImageBitmap(BitmapFactory.decodeStream(it))
                }
            }
        }
    }
}