package com.mandin.antoine.mydataanalyser.views.adapters

import android.graphics.Bitmap
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mandin.antoine.mydataanalyser.R
import com.mandin.antoine.mydataanalyser.facebook.model.Post
import com.mandin.antoine.mydataanalyser.tools.ImageTaskLoader
import com.mandin.antoine.mydataanalyser.tools.TaskRunner
import com.mandin.antoine.mydataanalyser.utils.Preferences
import kotlinx.android.synthetic.main.item_view_post_comment.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Adapter for browsing post one by one
 */
class PostsAdapter(private val postList: List<Post>) : RecyclerView.Adapter<PostsAdapter.PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_view_post_comment, parent, false)
        return PostViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.update(postList[position])
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    /**
     * Post view holder
     */
    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            if (facebookFolderPath == null)
                facebookFolderPath = Preferences.getFacebookFolderUri(itemView.context)
        }

        /**
         * Show post information
         */
        fun update(post: Post) {
            if (post.content == null || post.content.isEmpty()) {
                itemView.tvContent.visibility = View.GONE
            } else {
                itemView.tvContent.visibility = View.VISIBLE
                itemView.tvContent.text = post.content.trim()
            }
            itemView.tvDate.text = dateFormatter.format(post.date)
            itemView.tvWhere.text = post.where

            itemView.iv1.visibility = View.GONE
            itemView.iv2.visibility = View.GONE
            itemView.iv3.visibility = View.GONE

            for (i in 0..2) {
                if (i < post.medias.size && post.medias[i].uriFromRoot != null) {
                    TaskRunner().executeAsync(
                        ImageTaskLoader(
                            itemView.context,
                            Uri.parse(facebookFolderPath),
                            post.medias[i].uriFromRoot,
                            isThumbnail = true
                        ),
                        object : TaskRunner.Callback<Bitmap?> {
                            override fun onComplete(result: Bitmap?) {
                                when (i) {
                                    0 -> {
                                        itemView.iv1.visibility = View.VISIBLE
                                        itemView.iv1.setImageBitmap(result)
                                    }
                                    1 -> {
                                        itemView.iv2.visibility = View.VISIBLE
                                        itemView.iv2.setImageBitmap(result)
                                    }
                                    2 -> {
                                        itemView.iv3.visibility = View.VISIBLE
                                        itemView.iv3.setImageBitmap(result)
                                    }
                                }
                            }
                        });
                }
            }

        }

        companion object {
            val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
            var facebookFolderPath: String? = null
        }
    }
}