package com.mandin.antoine.mydataanalyser.utils

import android.content.Context

/**
 * Util class to facilitate saves and gets in/from Shared preferences
 */
object Preferences {
    const val PREF_NAME = "preferences"
    const val PREF_FACEBOOK_FOLDER_URI = "facebookFolderUri"

    /**
     * Save the facebook folder uri into shared preferences
     *
     * @param context context used to write
     * @param uri uri of the facebook folder data
     */
    fun saveFacebookFolderUri(context: Context, uri: String) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(PREF_FACEBOOK_FOLDER_URI, uri)
            .apply()
    }

    /**
     * retrieve the facebook folder uri from shared preferences
     *
     * @param context context used to read files
     */
    fun getFacebookFolderUri(context: Context): String? {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(PREF_FACEBOOK_FOLDER_URI, null)
    }

}