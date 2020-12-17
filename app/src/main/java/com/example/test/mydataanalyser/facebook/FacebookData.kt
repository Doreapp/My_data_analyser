package com.example.test.mydataanalyser.facebook

import androidx.documentfile.provider.DocumentFile
import com.example.test.mydataanalyser.utils.Debug

class FacebookData(val rootFolder: DocumentFile) {
    val TAG = "FacebookData"

    object Paths {
        const val PATH_ABOUT_YOU = "about_you"

        // TODO Add other paths (C:\Users\antoi\Downloads\tmp\facebook-AntoineMandin)
        const val PATH_MESSAGES = "messages"

        const val PATH_MESSAGES_ARCHIVED_THREADS = "archived_threds"

        // ...
        const val PATH_MESSAGES_INBOX = "inbox"
    }

    fun test() {
        Debug.i(TAG, "root exists ? ${rootFolder.exists()}")

        val f2 = rootFolder.findFile(Paths.PATH_MESSAGES)?.findFile(Paths.PATH_MESSAGES_INBOX)
        Debug.i(TAG, "file inbox $f2, exists ? ")
    }

    val inboxCount: Int?
        get() = rootFolder.findFile(Paths.PATH_MESSAGES)?.findFile(Paths.PATH_MESSAGES_INBOX)?.listFiles()?.size


}