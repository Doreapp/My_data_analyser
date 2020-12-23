package com.mandin.antoine.mydataanalyser.facebook.model.data

import com.mandin.antoine.mydataanalyser.facebook.model.Conversation
import com.mandin.antoine.mydataanalyser.facebook.model.Person
import java.util.*
import kotlin.collections.HashMap

/**
 * Class storing useful stats upon a conversation, for displaying
 */
class ConversationStats(conversation: Conversation) {
    val participantsMessageCount = HashMap<Person, Int>()
    val messageCountByYear = TreeMap<Date, Int>(classicDateComparator)
    val messageCountByMonth = TreeMap<Date, Int>(classicDateComparator)
    val messageCountByWeek = TreeMap<Date, Int>(classicDateComparator)

    init {
        for (message in conversation.messages) {
            //Increment message count of the participant
            message.person?.let { person ->
                when (val lastParticipantMessageCount = participantsMessageCount[person]) {
                    null -> participantsMessageCount[person] = 1
                    else -> participantsMessageCount[person] = lastParticipantMessageCount + 1
                }
            }

            message.sendingDate?.let { date ->
                with(Calendar.getInstance()) {
                    time = date
                    set(Calendar.MILLISECOND, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.HOUR_OF_DAY, 0)

                    val savedDay = get(Calendar.DAY_OF_WEEK)
                    set(Calendar.DAY_OF_WEEK, 1)
                    increment(messageCountByWeek, time)

                    set(Calendar.DAY_OF_WEEK, savedDay)
                    set(Calendar.DAY_OF_MONTH, 1)
                    increment(messageCountByMonth, time)

                    set(Calendar.MONTH, 0)
                    increment(messageCountByYear, time)
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

    object classicDateComparator : Comparator<Date> {
        override fun compare(o1: Date?, o2: Date?): Int {
            if (o1 == null || o2 == null)
                return 0
            return o1.compareTo(o2)
        }

    }
}
