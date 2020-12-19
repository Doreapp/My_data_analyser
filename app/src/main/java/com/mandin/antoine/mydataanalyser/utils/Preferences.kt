package com.mandin.antoine.mydataanalyser.utils

import android.content.Context

object Preferences {
    const val PREF_NAME = "preferences"
    const val PREF_FACEBOOK_ZIP_PATH = "facebookZipPath"

    fun saveFacebookZipPath(context: Context, path: String) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(PREF_FACEBOOK_ZIP_PATH, path)
            .apply()
    }


}