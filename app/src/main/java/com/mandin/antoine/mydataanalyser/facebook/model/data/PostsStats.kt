package com.mandin.antoine.mydataanalyser.facebook.model.data

import java.util.*
import kotlin.collections.HashMap

class PostsStats(postsData: PostsData) {
    val postCountByYear = TreeMap<Date, Int>(ConversationStats.classicDateComparator)
    val postCountByMonth = TreeMap<Date, Int>(ConversationStats.classicDateComparator)
    val postCountByWeek = TreeMap<Date, Int>(ConversationStats.classicDateComparator)
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
                    set(Calendar.MILLISECOND, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.HOUR_OF_DAY, 0)

                    val savedDay = get(Calendar.DAY_OF_WEEK)
                    set(Calendar.DAY_OF_WEEK, 1)
                    increment(postCountByWeek, time)

                    set(Calendar.DAY_OF_WEEK, savedDay)
                    set(Calendar.DAY_OF_MONTH, 1)
                    increment(postCountByMonth, time)

                    set(Calendar.MONTH, 0)
                    increment(postCountByYear, time)
                }

            }
        }
    }

    private fun increment(map: TreeMap<Date, Int>, date: Date) {
        when (val value = map[date]) {
            null -> map[date] = 1
            else -> map[date] = value + 1
        }
    }

    private fun increment(map: HashMap<String, Int>, str: String) {
        when (val value = map[str]) {
            null -> map[str] = 1
            else -> map[str] = value + 1
        }
    }
}