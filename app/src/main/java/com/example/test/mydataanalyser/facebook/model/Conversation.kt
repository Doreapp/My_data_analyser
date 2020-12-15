package com.example.test.mydataanalyser.facebook.model

data class Conversation(
    var participants: List<Person>,
    var messages: List<Message>,
    var title: String?,
    var isStillParticipant: Boolean = false
)
