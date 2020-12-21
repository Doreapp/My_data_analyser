package com.mandin.antoine.mydataanalyser.utils

object Constants {
    /**
     * Request code for the Permission to read the external storage
     */
    const val REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSION: Int = 1001

    /**
     * Request code for picking the facebook data folder
     */
    const val REQUEST_CODE_PICK_FACEBOOK_FOLDER: Int = 2001

    /**
     * Extra name of the conversation id
     */
    const val EXTRA_CONVERSATION_ID = "conversationId"

    /**
     * Extra name of the photo folder's uri
     */
    const val EXTRA_PHOTO_FOLDER_URI = "photoFolderUri"

    const val THUMBNAIL_SIZE = 200
}