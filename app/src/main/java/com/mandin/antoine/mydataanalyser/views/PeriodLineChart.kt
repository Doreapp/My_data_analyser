package com.mandin.antoine.mydataanalyser.views

import android.content.Context
import android.util.AttributeSet
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PeriodLineChart(context: Context, attrs: AttributeSet) : LineChart(context, attrs) {
    var messageCountByYear: Map<Date, Int>? = null
    var messageCountByMonth: Map<Date, Int>? = null
    var messageCountByWeek: Map<Date, Int>? = null
    var messageCountByDay: Map<Date, Int>? = null

    fun showMessageCountByYear() {
        messageCountByYear?.entries?.let {
            showEntries(it)
            xAxis.valueFormatter = YearValueFormatter
        }
    }

    fun showMessageCountByMonth() {
        messageCountByMonth?.entries?.let { set ->
            showEntries(set)
            xAxis.valueFormatter = MonthValueFormatter
        }
    }

    fun showMessageCountByWeek() {
        messageCountByWeek?.entries?.let {
            showEntries(it)
            xAxis.valueFormatter = WeekValueFormatter
        }
    }

    fun showMessageCountByDay() {
        messageCountByDay?.entries?.let {
            showEntries(it)
            xAxis.valueFormatter = DayValueFormatter
        }
    }

    private fun showEntries(rawEntries: Collection<Map.Entry<Date, Int>>) {
        val entries = ArrayList<Entry>()

        for (entry in rawEntries) {
            entries.add(Entry(entry.key.time.toFloat(), entry.value.toFloat()))
        }

        val dataset = LineDataSet(entries, "Message count")


        data = LineData(dataset)
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
        private val dateFormatter = SimpleDateFormat("Www/yy", Locale.ENGLISH)
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