package com.mandin.antoine.mydataanalyser.views

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.mandin.antoine.mydataanalyser.R

abstract class NumberedItemAdapter<T>(private var list: List<T>) :
    BaseAdapter() {

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
        val view = LayoutInflater.from(parent?.context)
            .inflate(R.layout.item_view_numbered_list, parent, false)
        view.findViewById<TextView>(R.id.tvName).text = getName(list[position])
        view.findViewById<TextView>(R.id.tvNumber).text = "" + getNumber(list[position])

        return view
    }

    abstract fun getName(value: T): String?

    abstract fun getNumber(value: T): Int
}