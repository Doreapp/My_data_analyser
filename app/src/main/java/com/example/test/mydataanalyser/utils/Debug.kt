package com.example.test.mydataanalyser.utils

import android.util.Log

object Debug {
    const val DEBUG = true

    fun i(tag: String?, msg: String) {
        if (DEBUG)
            Log.i(tag, msg)
    }
}