package com.mandin.antoine.mydataanalyser.facebook.model.data

import com.mandin.antoine.mydataanalyser.facebook.model.Conversation
import com.mandin.antoine.mydataanalyser.facebook.model.Person

/**
 * Class storing useful stats upon a conversation, for displaying
 */
class ConversationStats(conversation: Conversation) {
    var participantsMessageCount = HashMap<Person, Int>()
    //TODO some more stats

    init {
        for (message in conversation.messages) {
            //Increment message count of the participant
            message.person?.let { person ->
                val lastParticipantMessageCount = participantsMessageCount[person]
                when (lastParticipantMessageCount) {
                    null -> participantsMessageCount[person] = 1
                    else -> participantsMessageCount[person] = lastParticipantMessageCount + 1
                }
            }
        }
    }
}
