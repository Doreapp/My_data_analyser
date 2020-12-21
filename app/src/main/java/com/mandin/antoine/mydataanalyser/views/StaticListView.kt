package com.mandin.antoine.mydataanalyser.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Adapter
import androidx.appcompat.widget.LinearLayoutCompat
import com.mandin.antoine.mydataanalyser.R
import com.mandin.antoine.mydataanalyser.utils.Debug
import kotlinx.android.synthetic.main.view_numbered_list.view.*
import kotlin.math.max
import kotlin.math.min

/**
 * View of a list. Non-scrollable
 *
 * May contain a "Show more" button in order not to show every items at the beginning and let the
 * user increment the number of shown items.
 */
class StaticListView(context: Context, attrs: AttributeSet? = null) :
    LinearLayoutCompat(context, attrs) {
    val TAG = "StaticListView"

    /**
     * Is the "show more" button shown
     */
    var isShowMoreButtonVisible: Boolean
        set(value) {
            post {
                btnShowMore.visibility = if (value) VISIBLE else GONE
            }
        }
        get() = btnShowMore.visibility == VISIBLE

    /**
     * Are every items shown ?
     *
     * Equals if [shownItemsCount] = [Adapter.getCount]
     */
    var everyItemsShown: Boolean
        get() {
            return adapter == null || adapter!!.count == shownItemsCount
        }
        set(value) {
            if (value && adapter != null) {
                shownItemsCount = adapter!!.count
            }
        }

    /**
     * Current shown items count
     *
     * Will be between 0 and [Adapter.getCount]
     */
    var shownItemsCount: Int = 4
        set(value) {
            if (value < 0)
                return
            if (adapter == null) {
                field = value
            } else {
                field = min(value, adapter!!.count)
                updateDisplay()
            }
        }

    /**
     * How much the number of shown views increments on click on "show more"
     */
    var showItemsIncrementation: Int = 4
        set(value) {
            field = max(1, value)
        }

    /**
     * The adapter giving item views to show
     */
    var adapter: Adapter? = null
        set(value) {
            field = value
            if (value != null && shownItemsCount > value.count)
                shownItemsCount = value.count
            removeItemViews()
            updateDisplay()
        }

    init {
        LayoutInflater.from(context)
            .inflate(R.layout.view_numbered_list, this, true)

        orientation = VERTICAL

        btnShowMore.setOnClickListener {
            shownItemsCount += showItemsIncrementation
        }
    }

    /**
     * Update the item views shown --> Add views until [shownItemsCount] views are shown
     */
    fun updateDisplay() {
        Debug.i(TAG, "updateDisplay shownItemsCount=$shownItemsCount, isShowMoreButtonVisible=$isShowMoreButtonVisible")
        adapter?.let { adapter ->
            post {
                for (i in childCount - 1 until shownItemsCount) {
                    Debug.i(TAG, "add a view ($i th/$shownItemsCount)")
                    addView(adapter.getView(i, null, this), i)
                }
                isShowMoreButtonVisible = !everyItemsShown
                //invalidate()
            }
        }
    }

    /**
     * Remove every items views (it means that the "show more" button stays)
     */
    fun removeItemViews() {
        for (i in 0 until childCount - 1)
            removeViewAt(0)
    }

}