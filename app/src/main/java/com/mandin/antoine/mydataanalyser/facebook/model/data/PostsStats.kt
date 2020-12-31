package com.mandin.antoine.mydataanalyser.facebook.model.data

import com.mandin.antoine.mydataanalyser.utils.Utils
import java.util.*
import kotlin.collections.HashMap

class PostsStats(postsData: PostsData) : BaseStats() {
    val postCountByYear = TreeMap<Date, Int>(Utils.ClassicDateComparator)
    val postCountByMonth = TreeMap<Date, Int>(Utils.ClassicDateComparator)
    val postCountByWeek = TreeMap<Date, Int>(Utils.ClassicDateComparator)
    val whereCounts = HashMap<String, Int>()
    var photoCount = 0

    init {
        for (post in postsData.posts) {
            photoCount += post.medias.size

            post.where?.let {
                increment(whereCounts, it)
            }

            post.date?.let { date ->
                with(Calendar.getInstance()) {
                    time = date
                    set(Calendar.MILLISECOND, 500)
                    set(Calendar.SECOND, 30)
                    set(Calendar.MINUTE, 30)
                    set(Calendar.HOUR_OF_DAY, 12)

                    val savedDay = get(Calendar.DAY_OF_WEEK)
                    set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
                    increment(postCountByWeek, time)

                    set(Calendar.DAY_OF_WEEK, savedDay)
                    set(Calendar.DAY_OF_MONTH, 15)
                    increment(postCountByMonth, time)

                    set(Calendar.MONTH, 11)
                    increment(postCountByYear, time)
                }

            }
        }
    }

}