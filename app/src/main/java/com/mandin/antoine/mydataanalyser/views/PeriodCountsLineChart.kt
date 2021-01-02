package com.mandin.antoine.mydataanalyser.views

import android.content.Context
import android.util.AttributeSet
import com.mandin.antoine.mydataanalyser.R

/**
 * View of a Line Chart (Graph) of the evolution of a count through the time.
 * Propose several intervals of times (daily, weekly, monthly or yearly)
 *
 * @see R.layout.period_line_chart
 * @see R.array.spinnerPeriods
 */
class PeriodCountsLineChart(context: Context, attrs: AttributeSet) :
    PeriodLineChart<Int>(context, attrs) {
    private val TAG = "PeriodCountsLineChart"

}