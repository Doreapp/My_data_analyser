package com.mandin.antoine.mydataanalyser.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.LinearLayoutCompat
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.mandin.antoine.mydataanalyser.R
import com.mandin.antoine.mydataanalyser.utils.Debug
import kotlinx.android.synthetic.main.period_line_chart.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PeriodLineChart(context: Context, attrs: AttributeSet) : LinearLayoutCompat(context, attrs) {
    private val TAG = "PeriodLineChart"
    var messageCountByYear: Map<Date, Int>? = null
    var messageCountByMonth: Map<Date, Int>? = null
    var messageCountByWeek: Map<Date, Int>? = null
    var messageCountByDay: Map<Date, Int>? = null

    init {
        Debug.i(TAG, "<init>")
        LayoutInflater.from(context)
            .inflate(R.layout.period_line_chart, this, true)

        ArrayAdapter.createFromResource(
            context,
            R.array.spinnerPeriods,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinnerPeriod.adapter = adapter
        }

        spinnerPeriod.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                Debug.i(
                    TAG, "spinner -> onItemSelected (" +
                            "view=$view, position=$position, id=$id"
                )
                when (position) {
                    0 -> showMessageCountByYear()
                    1 -> showMessageCountByMonth()
                    2 -> showMessageCountByWeek()
                    3 -> showMessageCountByDay()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        with(lineChart) {
            description = null
        }
    }

    fun showMessageCountByYear() {
        Debug.i(TAG, "showMessageCountByYear")
        lineChart.xAxis.valueFormatter = YearValueFormatter
        showEntries(messageCountByYear?.entries)
    }

    fun showMessageCountByMonth() {
        Debug.i(TAG, "showMessageCountByMonth")
        lineChart.xAxis.valueFormatter = MonthValueFormatter
        showEntries(messageCountByMonth?.entries)
    }

    fun showMessageCountByWeek() {
        Debug.i(TAG, "showMessageCountByWeek")
        lineChart.xAxis.valueFormatter = WeekValueFormatter
        showEntries(messageCountByWeek?.entries)
    }

    fun showMessageCountByDay() {
        Debug.i(TAG, "showMessageCountByDay")
        lineChart.xAxis.valueFormatter = DayValueFormatter
        showEntries(messageCountByDay?.entries)
    }

    private fun showEntries(rawEntries: Collection<Map.Entry<Date, Int>>?) {
        Debug.i(TAG, "show Entries (${rawEntries?.size} entries)")
        when (rawEntries) {
            null -> clearChart()
            else -> {
                val entries = ArrayList<Entry>()

                for (entry in rawEntries) {
                    entries.add(Entry(entry.key.time.toFloat(), entry.value.toFloat()))
                }

                val dataset = LineDataSet(entries, "Message count")

                lineChart.data = LineData(dataset)
            }
        }
        lineChart.invalidate()
    }

    fun clearChart() {
        lineChart.clear()
    }


    object YearValueFormatter : ValueFormatter() {
        private val dateFormatter = SimpleDateFormat("yyyy", Locale.ENGLISH)
        override fun getFormattedValue(value: Float): String {
            return dateFormatter.format(Date(value.toLong()))
        }
    }

    object MonthValueFormatter : ValueFormatter() {
        private val dateFormatter = SimpleDateFormat("MMM yyyy", Locale.ENGLISH)
        override fun getFormattedValue(value: Float): String {
            return dateFormatter.format(Date(value.toLong()))
        }
    }

    object WeekValueFormatter : ValueFormatter() {
        private val dateFormatter = SimpleDateFormat("'W'ww/yy", Locale.ENGLISH)
        override fun getFormattedValue(value: Float): String {
            return dateFormatter.format(Date(value.toLong()))
        }
    }

    object DayValueFormatter : ValueFormatter() {
        private val dateFormatter = SimpleDateFormat("dd/MM/yy", Locale.ENGLISH)

        override fun getFormattedValue(value: Float): String {
            return dateFormatter.format(Date(value.toLong()))
        }
    }

}