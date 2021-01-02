package com.mandin.antoine.mydataanalyser.spotify.model.data

import com.mandin.antoine.mydataanalyser.tools.BaseStats
import com.mandin.antoine.mydataanalyser.utils.Utils
import java.util.*
import kotlin.collections.HashMap

/**
 * Stats about streams.
 * Contains evolutions of streams count and time by periods
 *
 * @see StreamsData
 */
class StreamsStats(streamsData: StreamsData) : BaseStats() {
    val streamCountByYear = TreeMap<Date, Int>(Utils.ClassicDateComparator)
    val streamCountByMonth = TreeMap<Date, Int>(Utils.ClassicDateComparator)
    val streamCountByWeek = TreeMap<Date, Int>(Utils.ClassicDateComparator)
    val artistCounts = HashMap<String, Int>()
    val trackCounts = HashMap<String, Int>()

    val streamTimeByYear = TreeMap<Date, Long>(Utils.ClassicDateComparator)
    val streamTimeByMonth = TreeMap<Date, Long>(Utils.ClassicDateComparator)
    val streamTimeByWeek = TreeMap<Date, Long>(Utils.ClassicDateComparator)
    val artistTimes = HashMap<String, Long>()
    val trackTimes = HashMap<String, Long>()

    var streamTime = 0L

    init {
        for (stream in streamsData.streams) {
            streamTime += stream.duration

            stream.artist?.let { artist ->
                increment(artistCounts, artist)
                increment(artistTimes, artist, stream.duration)
                stream.trackName?.let { track ->
                    increment(trackCounts, "$artist - $track")
                    increment(trackTimes, "$artist - $track", stream.duration)
                }

            }


            stream.date?.let { date ->
                with(Calendar.getInstance()) {
                    time = date
                    set(Calendar.MILLISECOND, 500)
                    set(Calendar.SECOND, 30)
                    set(Calendar.MINUTE, 30)
                    set(Calendar.HOUR_OF_DAY, 12)

                    val savedDay = get(Calendar.DAY_OF_WEEK)
                    set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
                    increment(streamCountByWeek, time)
                    increment(streamTimeByWeek, time, stream.duration)

                    set(Calendar.DAY_OF_WEEK, savedDay)
                    set(Calendar.DAY_OF_MONTH, 15)
                    increment(streamCountByMonth, time)
                    increment(streamTimeByMonth, time, stream.duration)

                    set(Calendar.MONTH, 11)
                    increment(streamCountByYear, time)
                    increment(streamTimeByYear, time, stream.duration)
                }

            }
        }
    }
}