package com.mandin.antoine.mydataanalyser.facebook.model

import java.util.*

data class Message(
    var id: Long?,
    var person: Person?,
    var sendingDate: Date?,
    var content: String?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Message

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}