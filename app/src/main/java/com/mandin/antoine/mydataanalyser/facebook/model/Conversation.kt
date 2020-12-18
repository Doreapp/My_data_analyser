package com.mandin.antoine.mydataanalyser.facebook.model

data class Conversation(
    var participants: HashSet<Person>,
    var messages: ArrayList<Message>,
    var title: String?,
    var isStillParticipant: Boolean = false
) {
    fun import(other: Conversation) {
        messages.addAll(other.messages)
    }
}
