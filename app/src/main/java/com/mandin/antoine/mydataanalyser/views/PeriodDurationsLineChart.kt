package com.mandin.antoine.mydataanalyser.views

import android.content.Context
import android.util.AttributeSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.mandin.antoine.mydataanalyser.R
import com.mandin.antoine.mydataanalyser.utils.Utils

/**
 * View of a Line Chart (Graph) of the evolution of a duration sum through the time.
 * Propose several intervals of times (daily, weekly, monthly or yearly)
 *
 * @see R.layout.period_line_chart
 * @see R.array.spinnerPeriods
 */
class PeriodDurationsLineChart(context: Context, attrs: AttributeSet) :
    PeriodLineChart<Long>(context, attrs) {
    private val TAG = "PeriodDurationsLineChart"

    override fun yValueFormatter(): ValueFormatter? {
        return object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return Utils.formatDurationUntilHour(value.toLong())
            }
        }
    }
}