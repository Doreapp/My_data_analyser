package com.mandin.antoine.mydataanalyser.views

import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class ImageAdapter(private var imageIdList: List<Int>) : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ImageView(parent.context))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        (holder.itemView as ImageView).setImageResource(imageIdList[position])
    }

    override fun getItemCount(): Int {
        return imageIdList.size
    }

    inner class ViewHolder(itemView: ImageView) : RecyclerView.ViewHolder(itemView) {

    }
}