package com.mandin.antoine.mydataanalyser.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.mandin.antoine.mydataanalyser.R
import com.mandin.antoine.mydataanalyser.utils.Utils

/**
 * Adapter constructing a list of item numbered
 *
 * (May need to create a layout going with this adapter)
 */
abstract class DurationItemAdapter<T>(private var list: List<T>) :
    BaseAdapter() {

    private var numberSum: Long? = null

    var showPercentage: Boolean
        set(value) {
            if (value) {
                numberSum = 0
                for (item in list)
                    numberSum = numberSum!! + getDuration(item)
            } else {
                numberSum = null
            }
        }
        get() = numberSum != null

    init {
        list = list.sortedWith { t1, t2 ->
            -getDuration(t1).compareTo(getDuration(t2))
        }
    }

    override fun getItem(position: Int): T {
        return list[position]
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun getItemId(position: Int): Long {
        return getItemId(position).hashCode().toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view = convertView ?: LayoutInflater.from(parent?.context)
            .inflate(
                R.layout.item_view_numbered_list,
                parent, false
            )

        view.findViewById<TextView>(R.id.tvName).text = getName(list[position])

        val duration = getDuration(list[position])
        view.findViewById<TextView>(R.id.tvNumber).text = formatDuration(duration) +
                if (showPercentage) " (${duration * 100 / numberSum!!}%)"
                else ""

        return view
    }

    /**
     * Function returning the name to show for a `T` item
     * @param value item to retrieve the name for
     */
    abstract fun getName(value: T): String?

    /**
     * Function returning the number for a `T` item
     * @param value item to retrieve the number for
     */
    abstract fun getDuration(value: T): Long

    /**
     * overridable function used to format a duration
     *
     * @param duration duration, founded from abstract function [getDuration]
     * @return a formatted duration, by default [Utils.formatDurationUntilHour]
     */
    open fun formatDuration(duration: Long): String {
        return Utils.formatDurationUntilHour(duration)
    }
}