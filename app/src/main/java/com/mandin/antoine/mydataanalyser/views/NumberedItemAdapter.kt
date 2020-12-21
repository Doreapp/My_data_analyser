package com.mandin.antoine.mydataanalyser.views

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.mandin.antoine.mydataanalyser.R

/**
 * Adapter constructing a list of item numbered
 *
 * (May need to create a layout going with this adapter)
 */
abstract class NumberedItemAdapter<T>(private var list: List<T>) :
    BaseAdapter() {

    private var numberSum: Int? = null

    var showPercentage: Boolean
        set(value) {
            if (value) {
                numberSum = 0
                for (item in list)
                    numberSum = numberSum!! + getNumber(item)
            } else {
                numberSum = null
            }
        }
        get() = numberSum != null

    init {
        list = list.sortedWith { t1, t2 ->
            -getNumber(t1).compareTo(getNumber(t2))
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

        val number = getNumber(list[position])
        view.findViewById<TextView>(R.id.tvNumber).text = "$number" +
                if (showPercentage) " (${number * 100 / numberSum!!}%)"
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
    abstract fun getNumber(value: T): Int
}