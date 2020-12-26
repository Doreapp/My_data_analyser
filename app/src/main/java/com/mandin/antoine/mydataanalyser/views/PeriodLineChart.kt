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

/**
 * View of a Line Chart (Graph) of the evolution of something throw the time
 * Propose several intervals of times (daily, weekly, monthly or yearly)
 *
 * @see R.layout.period_line_chart
 * @see R.array.spinnerPeriods
 */
class PeriodLineChart(context: Context, attrs: AttributeSet) : LinearLayoutCompat(context, attrs) {
    private val TAG = "PeriodLineChart"
    var countsYearly: Map<Date, Int>? = null
    var countsMonthly: Map<Date, Int>? = null
    var countsWeekly: Map<Date, Int>? = null
    var countsDaily: Map<Date, Int>? = null

    var lineLabel = ""

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
                    0 -> showCountsYearly()
                    1 -> showCountsMonthly()
                    2 -> showCountsWeekly()
                    3 -> showCountsDaily()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        with(lineChart) {
            description = null
        }
    }

    /**
     * Show counts yearly on graph.
     * May be called programmatically of by the spinner [R.id.spinnerPeriod]
     * If null, show an empty graph
     */
    fun showCountsYearly() {
        Debug.i(TAG, "showMessageCountByYear")
        lineChart.xAxis.valueFormatter = YearValueFormatter
        showEntries(countsYearly?.entries)
    }

    /**
     * Show counts monthly on graph.
     * May be called programmatically of by the spinner [R.id.spinnerPeriod]
     * If null, show an empty graph
     */
    fun showCountsMonthly() {
        Debug.i(TAG, "showMessageCountByMonth")
        lineChart.xAxis.valueFormatter = MonthValueFormatter
        showEntries(countsMonthly?.entries)
    }

    /**
     * Show counts weekly on graph.
     * May be called programmatically of by the spinner [R.id.spinnerPeriod]
     * If null, show an empty graph
     */
    fun showCountsWeekly() {
        Debug.i(TAG, "showMessageCountByWeek")
        lineChart.xAxis.valueFormatter = WeekValueFormatter
        showEntries(countsWeekly?.entries)
    }

    /**
     * Show counts daily on graph.
     * May be called programmatically of by the spinner [R.id.spinnerPeriod]
     * If null, show an empty graph
     */
    fun showCountsDaily() {
        Debug.i(TAG, "showMessageCountByDay")
        lineChart.xAxis.valueFormatter = DayValueFormatter
        showEntries(countsDaily?.entries)
    }

    /**
     * Show entries into the graph. If null, show an empty graph using [clearChart]
     */
    private fun showEntries(rawEntries: Collection<Map.Entry<Date, Int>>?) {
        Debug.i(TAG, "show Entries (${rawEntries?.size} entries)")
        when (rawEntries) {
            null -> clearChart()
            else -> {
                val entries = ArrayList<Entry>()

                for (entry in rawEntries) {
                    entries.add(Entry(entry.key.time.toFloat(), entry.value.toFloat()))
                }

                val dataset = LineDataSet(entries, lineLabel)

                lineChart.data = LineData(dataset)
            }
        }
        lineChart.invalidate()
    }

    /**
     * Clear the graph : show an empty chart
     */
    fun clearChart() {
        lineChart.clear()
    }

    /**
     * Formatter for X Axis : format a date into its year
     */
    object YearValueFormatter : ValueFormatter() {
        private val dateFormatter = SimpleDateFormat("yyyy", Locale.ENGLISH)
        override fun getFormattedValue(value: Float): String {
            return dateFormatter.format(Date(value.toLong()))
        }
    }

    /**
     * Formatter for X Axis : format a date into its Month + year
     */
    object MonthValueFormatter : ValueFormatter() {
        private val dateFormatter = SimpleDateFormat("MMM yyyy", Locale.ENGLISH)
        override fun getFormattedValue(value: Float): String {
            return dateFormatter.format(Date(value.toLong()))
        }
    }


    /**
     * Formatter for X Axis : format a date into its week + year
     */
    object WeekValueFormatter : ValueFormatter() {
        private val dateFormatter = SimpleDateFormat("'W'ww/yy", Locale.ENGLISH)
        override fun getFormattedValue(value: Float): String {
            return dateFormatter.format(Date(value.toLong()))
        }
    }

    /**
     * Formatter for X Axis : format a date into its day (day+month+year)
     */
    object DayValueFormatter : ValueFormatter() {
        private val dateFormatter = SimpleDateFormat("dd/MM/yy", Locale.ENGLISH)

        override fun getFormattedValue(value: Float): String {
            return dateFormatter.format(Date(value.toLong()))
        }
    }

}