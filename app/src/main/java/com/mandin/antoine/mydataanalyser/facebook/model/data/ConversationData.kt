package com.mandin.antoine.mydataanalyser.facebook.model.data

import com.mandin.antoine.mydataanalyser.facebook.model.Person
import java.util.*

data class ConversationData(
    var id: Long?,
    var title: String?,
    var path: String?,
    var participants: Set<Person>?,
    var isStillParticipant: Boolean?,
    var messageCount: Int,
    var photoCount: Int,
    var audioCount: Int,
    var gifCount: Int,
    var creationDate: Date?
)
