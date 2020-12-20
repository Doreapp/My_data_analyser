package com.mandin.antoine.mydataanalyser.facebook

object CharsetsUtils {
    fun translateIsoToUtf(str: String): String {
        return String(str.toByteArray(Charsets.ISO_8859_1), Charsets.UTF_8)
    }
}