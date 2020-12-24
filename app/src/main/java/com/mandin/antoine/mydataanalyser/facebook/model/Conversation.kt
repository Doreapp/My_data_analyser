package com.mandin.antoine.mydataanalyser.facebook.model

import java.util.*

data class Conversation(
    var id: Long?,
    var participants: HashSet<Person>,
    var messages: ArrayList<Message>,
    var medias: ArrayList<Media>,
    var title: String?,
    var isStillParticipant: Boolean = false
) {
    /**
     * Add every message from `other` into this conversation
     */
    fun import(other: Conversation) {
        messages.addAll(other.messages)
        medias.addAll(other.medias)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Conversation

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    fun findCreationDate(): Date? {
        var currentLowest = messages[0].sendingDate
        messages.forEach { message ->
            if (message.sendingDate?.before(currentLowest) == true)
                currentLowest = message.sendingDate
        }
        return currentLowest
    }
}
