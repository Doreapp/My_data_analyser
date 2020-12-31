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
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
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
    var countsYearly: TreeMap<Date, Int>? = null
    var countsMonthly: TreeMap<Date, Int>? = null
    var countsWeekly: TreeMap<Date, Int>? = null
    var countsDaily: TreeMap<Date, Int>? = null
    private var intervals: List<Date>? = null

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
            setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    Debug.i(
                        TAG, "onValueSelected : " +
                                "x = ${DayValueFormatter().getFormattedValue(e!!.x)}, y = ${e.y}"
                    )
                }

                override fun onNothingSelected() {

                }
            })
        }
    }

    /**
     * Show counts yearly on graph.
     * May be called programmatically of by the spinner [R.id.spinnerPeriod]
     * If null, show an empty graph
     */
    fun showCountsYearly() {
        Debug.i(TAG, "showMessageCountByYear")
        //showEntries(countsYearly?.entries)
        when (countsYearly) {
            null -> clearChart()
            else -> showMap(countsYearly!!, getYearsIntervals(countsYearly!!.firstKey(), countsYearly!!.lastKey()))
        }
        lineChart.xAxis.valueFormatter = YearValueFormatter()
        lineChart.invalidate()
    }

    /**
     * Show counts monthly on graph.
     * May be called programmatically of by the spinner [R.id.spinnerPeriod]
     * If null, show an empty graph
     */
    fun showCountsMonthly() {
        Debug.i(TAG, "showMessageCountByMonth")
        //showEntries(countsMonthly?.entries)
        when (countsMonthly) {
            null -> clearChart()
            else -> showMap(countsMonthly!!, getMonthsIntervals(countsMonthly!!.firstKey(), countsMonthly!!.lastKey()))
        }
        lineChart.xAxis.valueFormatter = MonthValueFormatter()
        lineChart.invalidate()
    }

    /**
     * Show counts weekly on graph.
     * May be called programmatically of by the spinner [R.id.spinnerPeriod]
     * If null, show an empty graph
     */
    fun showCountsWeekly() {
        Debug.i(TAG, "showMessageCountByWeek")
        //showEntries(countsWeekly?.entries)
        when (countsWeekly) {
            null -> clearChart()
            else -> showMap(countsWeekly!!, getWeeksIntervals(countsWeekly!!.firstKey(), countsWeekly!!.lastKey()))
        }
        lineChart.xAxis.valueFormatter = WeekValueFormatter()
        lineChart.invalidate()
    }

    /**
     * Show counts daily on graph.
     * May be called programmatically of by the spinner [R.id.spinnerPeriod]
     * If null, show an empty graph
     */
    fun showCountsDaily() {
        Debug.i(TAG, "showMessageCountByDay")
        //showEntries(countsDaily?.entries)
        when (countsDaily) {
            null -> clearChart()
            else -> showMap(countsDaily!!, getDaysIntervals(countsDaily!!.firstKey(), countsDaily!!.lastKey()))
        }
        lineChart.xAxis.valueFormatter = DayValueFormatter()
        lineChart.invalidate()
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
     * Display map entries into the graph
     * @param map The map containing data to display
     * @param intervals intervals that must be displayed (may contains keys that aren't in [map] and may not contains every keys of [map] neither
     *
     */
    private fun showMap(map: TreeMap<Date, Int>, intervals: List<Date>) {
        Debug.i(TAG, "showMap (${map.size} entries)")
        this.intervals = intervals
        val entries = ArrayList<Entry>()

        for ((index, interval) in intervals.withIndex()) {
            var value = map[interval]
            if (value == null) value = 0
            entries.add(
                Entry(
                    index.toFloat(),
                    value.toFloat()
                )
            )
        }

        val dataset = LineDataSet(entries, lineLabel)

        lineChart.xAxis.granularity = 1f
        lineChart.data = LineData(dataset)
    }

    /**
     * Clear the graph : show an empty chart
     */
    fun clearChart() {
        lineChart.clear()
    }

    /**
     * Build yearly intervals.
     * It's a list of dates, separated by one year
     * @param first first Year date of the list
     * @param last last Year date of the list
     */
    private fun getYearsIntervals(first: Date, last: Date): List<Date> {
        val cal = Calendar.getInstance()
        cal.time = last
        val lastYear = cal.get(Calendar.YEAR)
        cal.time = first
        val firstYear = cal.get(Calendar.YEAR)

        val result = ArrayList<Date>()
        for (year in firstYear..lastYear) {
            cal.set(Calendar.YEAR, year)
            result.add(cal.time)
        }
        return result
    }

    /**
     * Build monthly intervals.
     * It's a list of dates, separated by one month
     * @param first first month date of the list
     * @param last last month date of the list
     */
    private fun getMonthsIntervals(first: Date, last: Date): List<Date> {
        val cal = Calendar.getInstance()
        cal.time = last
        val lastMonth = cal.get(Calendar.YEAR) * 12 + cal.get(Calendar.MONTH)

        cal.time = first
        val firstMonth = cal.get(Calendar.YEAR) * 12 + cal.get(Calendar.MONTH)

        val result = ArrayList<Date>()
        for (month in firstMonth..lastMonth) {
            cal.set(Calendar.YEAR, month / 12)
            cal.set(Calendar.MONTH, month % 12)
            result.add(cal.time)
        }
        return result
    }

    /**
     * Build weekly intervals.
     * It's a list of dates, separated by one week
     * @param first first week date of the list
     * @param last last week date of the list
     */
    private fun getWeeksIntervals(first: Date, last: Date): List<Date> {
        val cal = Calendar.getInstance()
        cal.time = last
        val lastYear = cal.get(Calendar.YEAR)
        val lastWeek = cal.get(Calendar.WEEK_OF_YEAR)

        cal.time = first
        val firstYear = cal.get(Calendar.YEAR)
        val firstWeek = cal.get(Calendar.WEEK_OF_YEAR)

        val result = ArrayList<Date>()
        for (year in firstYear..lastYear) {
            cal.set(Calendar.YEAR, year)
            val minWeek = if (year == firstYear) firstWeek else cal.getActualMinimum(Calendar.WEEK_OF_YEAR)
            val maxWeek = if (year == lastYear) lastWeek else cal.getActualMaximum(Calendar.WEEK_OF_YEAR)
            for (week in minWeek..maxWeek) {
                cal.set(Calendar.WEEK_OF_YEAR, week)
                result.add(cal.time)
            }
        }
        return result
    }

    /**
     * Build daily intervals.
     * It's a list of dates, separated by one day
     * @param first first day date of the list
     * @param last last day date of the list
     */
    private fun getDaysIntervals(first: Date, last: Date): List<Date> {
        val cal = Calendar.getInstance()
        cal.time = last
        val lastYear = cal.get(Calendar.YEAR)
        val lastDay = cal.get(Calendar.DAY_OF_YEAR)

        cal.time = first
        val firstYear = cal.get(Calendar.YEAR)
        val firstDay = cal.get(Calendar.DAY_OF_YEAR)

        val result = ArrayList<Date>()
        for (year in firstYear..lastYear) {
            cal.set(Calendar.YEAR, year)
            val minDay = if (year == firstYear) firstDay else cal.getActualMinimum(Calendar.DAY_OF_YEAR)
            val maxDay = if (year == lastYear) lastDay else cal.getActualMaximum(Calendar.DAY_OF_YEAR)
            for (day in minDay..maxDay) {
                cal.set(Calendar.DAY_OF_YEAR, day)
                result.add(cal.time)
            }
        }
        return result
    }


    /**
     * Formatter for X Axis : format a date into its year
     */
    inner class YearValueFormatter : ValueFormatter() {
        private val dateFormatter = SimpleDateFormat("yyyy", Locale.ENGLISH)
        override fun getFormattedValue(value: Float): String {
            intervals?.let {
                if (value < 0 || value >= it.size) {
                    return "??"
                }
                return dateFormatter.format(it[value.toInt()])
            }
            return dateFormatter.format(Date(value.toLong()))
        }
    }

    /**
     * Formatter for X Axis : format a date into its Month + year
     */
    inner class MonthValueFormatter : ValueFormatter() {
        private val dateFormatter = SimpleDateFormat("MMM yyyy", Locale.ENGLISH)
        override fun getFormattedValue(value: Float): String {
            intervals?.let {
                if (value < 0 || value >= it.size) {
                    return "??"
                }
                return dateFormatter.format(it[value.toInt()])
            }
            return dateFormatter.format(Date(value.toLong()))
        }
    }

    /**
     * Formatter for X Axis : format a date into its week + year
     */
    inner class WeekValueFormatter : ValueFormatter() {
        private val dateFormatter = SimpleDateFormat("'W'ww/yy", Locale.ENGLISH)
        override fun getFormattedValue(value: Float): String {
            intervals?.let {
                if (value < 0 || value >= it.size) {
                    return "??"
                }
                return dateFormatter.format(it[value.toInt()])
            }
            return dateFormatter.format(Date(value.toLong()))
        }
    }

    /**
     * Formatter for X Axis : format a date into its day (day+month+year)
     */
    inner class DayValueFormatter : ValueFormatter() {
        private val dateFormatter = SimpleDateFormat("dd/MM/yy", Locale.ENGLISH)

        override fun getFormattedValue(value: Float): String {
            intervals?.let {
                if (value < 0 || value >= it.size) {
                    return "??"
                }
                return dateFormatter.format(it[value.toInt()])
            }
            return dateFormatter.format(Date(value.toLong()))
        }
    }

}