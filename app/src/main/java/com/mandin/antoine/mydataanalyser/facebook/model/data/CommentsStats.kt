package com.mandin.antoine.mydataanalyser.facebook.model.data

import com.mandin.antoine.mydataanalyser.tools.BaseStats
import com.mandin.antoine.mydataanalyser.utils.Utils
import java.util.*

/**
 * Statistics about facebook comments
 */
class CommentsStats(data: CommentsData) : BaseStats() {
    val commentCountByYear = TreeMap<Date, Int>(Utils.ClassicDateComparator)
    val commentCountByMonth = TreeMap<Date, Int>(Utils.ClassicDateComparator)
    val commentCountByWeek = TreeMap<Date, Int>(Utils.ClassicDateComparator)
    var photoCount = 0
    val whereCounts = HashMap<String, Int>()
    val groupCounts = HashMap<String, Int>()

    init {
        for (comment in data.comments) {
            comment.medias?.let {
                photoCount += it.size
            }

            comment.where?.let {
                increment(whereCounts, it)
            }

            comment.group?.let {
                increment(groupCounts, it)
            }

            comment.date?.let { date ->
                with(Calendar.getInstance()) {
                    time = date
                    set(Calendar.MILLISECOND, 500)
                    set(Calendar.SECOND, 30)
                    set(Calendar.MINUTE, 30)
                    set(Calendar.HOUR_OF_DAY, 12)

                    val savedDay = get(Calendar.DAY_OF_WEEK)
                    set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
                    increment(commentCountByWeek, time)

                    set(Calendar.DAY_OF_WEEK, savedDay)
                    set(Calendar.DAY_OF_MONTH, 15)
                    increment(commentCountByMonth, time)

                    set(Calendar.MONTH, 11)
                    increment(commentCountByYear, time)
                }
            }
        }
    }

}