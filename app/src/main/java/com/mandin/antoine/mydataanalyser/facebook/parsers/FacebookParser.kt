package com.mandin.antoine.mydataanalyser.facebook.parsers

import android.util.JsonReader
import com.mandin.antoine.mydataanalyser.facebook.CharsetsUtils
import com.mandin.antoine.mydataanalyser.tools.Parser

/**
 * Abstract parser.
 *
 * Contains base method [readJson] and useful methods such as [nextString]
 */
abstract class FacebookParser<T> : Parser<T>() {

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