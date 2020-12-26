package com.mandin.antoine.mydataanalyser.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mandin.antoine.mydataanalyser.R
import com.mandin.antoine.mydataanalyser.facebook.model.Post
import kotlinx.android.synthetic.main.item_view_post.view.*
import java.text.SimpleDateFormat
import java.util.*

class PostsAdapter(private val postList: List<Post>) : RecyclerView.Adapter<PostsAdapter.PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_view_post, parent, false)
        return PostViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.update(postList[position])
    }

    override fun getItemCount(): Int {
        return postList.size
    }


    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun update(post: Post) {
            itemView.tvContent.text = post.content
            itemView.tvDate.text = dateFormatter.format(post.date)
            itemView.tvWhere.text = post.where

            //TODO show image (medias)
        }

        companion object {
            val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
        }
    }
}