package com.mandin.antoine.mydataanalyser.tools

import android.util.JsonReader
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
}