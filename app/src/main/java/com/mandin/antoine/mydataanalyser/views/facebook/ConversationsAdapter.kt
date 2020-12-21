package com.mandin.antoine.mydataanalyser.views.facebook

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mandin.antoine.mydataanalyser.ConversationActivity
import com.mandin.antoine.mydataanalyser.R
import com.mandin.antoine.mydataanalyser.facebook.model.data.ConversationData
import com.mandin.antoine.mydataanalyser.utils.Constants

/**
 * Adapter used to display a list of conversations
 *
 * @see ConversationData
 * @see RecyclerView.Adapter
 */
class ConversationsAdapter(private var conversations: List<ConversationData>?) :
    RecyclerView.Adapter<ConversationsAdapter.ViewHolder>() {

    init {
        // Sort by message count descending
        sortBy(SortingType.MessageCount, false)
    }

    /**
     * Sorting type : by date, by message count or by alphabetical order
     */
    enum class SortingType { Date, MessageCount, Alphabetical }

    /**
     * Create an empty view holder
     */
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ConversationsAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_view_conversation, parent, false)
        )
    }

    /**
     * Fulfill the view holder display
     */
    override fun onBindViewHolder(
        holder: ConversationsAdapter.ViewHolder,
        position: Int
    ) {
        val conv = conversations?.get(position)
        holder.tvTitle.text = conv?.title

        holder.tvStats.text =
            "${conv?.messageCount} messages - Creating on ${conv?.creationDate}"

        holder.itemView.setOnClickListener { view ->
            val intent = Intent(view.context, ConversationActivity::class.java)
            intent.putExtra(Constants.EXTRA_CONVERSATION_ID, conv?.id)

            view.context.startActivity(intent)
        }
    }

    /**
     * @return the number of items
     */
    override fun getItemCount(): Int {
        conversations?.let { return it.size }
        return 0
    }

    /**
     * Sort the list by a sorting type
     *
     * @param sortingType by which parameter should we sort the list
     * @param ascending do we sort ascending of descending (using false)
     */
    fun sortBy(sortingType: SortingType, ascending: Boolean = true) {
        when (sortingType) {
            SortingType.Date -> {
                conversations = conversations?.sortedWith { c1, c2 ->
                    if (c2.creationDate != null && c1.creationDate != null) {
                        if (ascending)
                            c1.creationDate!!.compareTo(c2.creationDate)
                        else
                            -c1.creationDate!!.compareTo(c2.creationDate)
                    } else
                        0
                }
            }
            SortingType.MessageCount -> {
                conversations = conversations?.sortedWith { c1, c2 ->
                    if (ascending)
                        c1.messageCount.compareTo(c2.messageCount)
                    else
                        -c1.messageCount.compareTo(c2.messageCount)
                }
            }
            SortingType.Alphabetical -> {
                conversations = conversations?.sortedWith { c1, c2 ->
                    if (c2.title != null && c1.title != null) {
                        if (ascending)
                            c1.title!!.compareTo(c2.title!!)
                        else
                            -c1.title!!.compareTo(c2.title!!)
                    } else
                        0
                }
            }
        }
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvStats: TextView = itemView.findViewById(R.id.tvStats)
    }
}