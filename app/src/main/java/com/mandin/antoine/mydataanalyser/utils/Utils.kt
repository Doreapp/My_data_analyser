package com.mandin.antoine.mydataanalyser.utils


object Utils {
    const val ZIP_CONTENT_TYPE = "application/zip"

    fun round2Digit(value: Float): Float {
        return (value * 100).toInt() / 100f
    }
}