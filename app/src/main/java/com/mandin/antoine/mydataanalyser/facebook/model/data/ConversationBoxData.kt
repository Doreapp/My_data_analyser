package com.mandin.antoine.mydataanalyser.facebook.model.data

data class ConversationBoxData(
    var archivedThreads: List<ConversationData>?,
    var filteredThreads: List<ConversationData>?,
    var inbox: List<ConversationData>?,
    var messageRequest: List<ConversationData>?,
    // TODO Stickers used
)
