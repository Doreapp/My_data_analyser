package com.mandin.antoine.mydataanalyser.facebook

object CharsetsUtils {

    /**
     * Translate a ISO string to an UTF-8 string.
     * Needed for reading json
     *
     * @see Charsets.ISO_8859_1
     * @see Charsets.UTF_8
     */
    fun translateIsoToUtf(str: String): String {
        return String(str.toByteArray(Charsets.ISO_8859_1), Charsets.UTF_8)
    }
}