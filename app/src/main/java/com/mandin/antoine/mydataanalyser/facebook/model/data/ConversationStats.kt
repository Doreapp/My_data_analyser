package com.mandin.antoine.mydataanalyser.facebook.model.data

import com.mandin.antoine.mydataanalyser.facebook.model.Conversation
import com.mandin.antoine.mydataanalyser.facebook.model.Person
import com.mandin.antoine.mydataanalyser.tools.BaseStats
import com.mandin.antoine.mydataanalyser.utils.Utils
import java.util.*
import kotlin.collections.HashMap

/**
 * Class storing useful stats upon a conversation, for displaying
 */
class ConversationStats(conversation: Conversation) : BaseStats() {
    val participantsMessageCount = HashMap<Person, Int>()
    val messageCountByYear = TreeMap<Date, Int>(Utils.ClassicDateComparator)
    val messageCountByMonth = TreeMap<Date, Int>(Utils.ClassicDateComparator)
    val messageCountByWeek = TreeMap<Date, Int>(Utils.ClassicDateComparator)

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
}
