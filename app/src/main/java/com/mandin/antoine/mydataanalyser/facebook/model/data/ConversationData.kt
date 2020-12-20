package com.mandin.antoine.mydataanalyser.facebook.model.data

import android.net.Uri
import com.mandin.antoine.mydataanalyser.facebook.model.Person
import java.util.*

data class ConversationData(
    var id: Long?,
    var title: String?,
    var uri: Uri?,
    var participants: Set<Person>?,
    var isStillParticipant: Boolean?,
    var messageCount: Int,
    var photoCount: Int,
    var audioCount: Int,
    var gifCount: Int,
    var creationDate: Date?
)
