package com.mandin.antoine.mydataanalyser.facebook.parsers

import android.util.JsonReader
import com.mandin.antoine.mydataanalyser.facebook.CharsetsUtils
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

/**
 * Abstract parser.
 *
 * Contains base method [readJson] and useful methods such as [nextString]
 */
abstract class Parser<T> {
    @Throws(IOException::class)
    fun readJson(input: InputStream): T {
        val reader = JsonReader(InputStreamReader(input, Charsets.UTF_8))
        return reader.use {
            readWhole(reader)
        }
    }

    abstract fun readWhole(reader: JsonReader): T

    /**
     * Read next strinf from [reader] with the right encoding
     *
     * @see CharsetsUtils.translateIsoToUtf
     */
    protected final fun nextString(reader: JsonReader): String {
        return CharsetsUtils.translateIsoToUtf(reader.nextString())
    }

    /**
     * Substring [str] after [subStr] if contained, else returns null
     */
    protected fun substringAfter(str: String, subStr: String): String? {
        val index = str.indexOf(subStr)
        if (index >= 0)
            return str.substring(index + subStr.length)
        return null
    }
}