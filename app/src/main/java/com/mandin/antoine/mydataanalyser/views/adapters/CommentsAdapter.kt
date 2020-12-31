package com.mandin.antoine.mydataanalyser.views.adapters

import android.graphics.Bitmap
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mandin.antoine.mydataanalyser.R
import com.mandin.antoine.mydataanalyser.facebook.model.Comment
import com.mandin.antoine.mydataanalyser.tools.ImageTaskLoader
import com.mandin.antoine.mydataanalyser.tools.TaskRunner
import com.mandin.antoine.mydataanalyser.utils.Preferences
import kotlinx.android.synthetic.main.item_view_post_comment.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Adapter to browse comments one by one
 */
class CommentsAdapter(private val commentList: List<Comment>) :
    RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_view_post_comment, parent, false)
        return CommentViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.update(commentList[position])
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

    /**
     * Comment view holder
     */
    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            if (facebookFolderPath == null)
                facebookFolderPath = Preferences.getFacebookFolderUri(itemView.context)
        }

        /**
         * Display [comment] information
         */
        fun update(comment: Comment) {
            if (comment.content == null || comment.content.isEmpty()) {
                itemView.tvContent.visibility = View.GONE
            } else {
                itemView.tvContent.visibility = View.VISIBLE
                itemView.tvContent.text = comment.content.trim()
            }
            itemView.tvDate.text = dateFormatter.format(comment.date)

            var whereText = comment.where
            comment.group?.let { whereText += "\n($it)" }
            itemView.tvWhere.text = whereText

            itemView.iv1.visibility = View.GONE
            itemView.iv2.visibility = View.GONE
            itemView.iv3.visibility = View.GONE

            for (i in 0..2) {
                val size = if (comment.medias == null) 0 else comment.medias.size
                if (i < size && comment.medias!![i].uriFromRoot != null) {
                    TaskRunner().executeAsync(
                        ImageTaskLoader(
                            itemView.context,
                            Uri.parse(facebookFolderPath),
                            comment.medias[i].uriFromRoot,
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
                        })
                }
            }

        }

        companion object {
            val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
            var facebookFolderPath: String? = null
        }
    }
}