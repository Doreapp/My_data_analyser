package com.mandin.antoine.mydataanalyser.facebook.model

data class Conversation(
    var id: Long?,
    var participants: HashSet<Person>,
    var messages: ArrayList<Message>,
    var title: String?,
    var isStillParticipant: Boolean = false
) {
    fun import(other: Conversation) {
        messages.addAll(other.messages)
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
}
