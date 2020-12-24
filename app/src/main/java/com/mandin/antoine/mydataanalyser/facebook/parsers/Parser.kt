package com.mandin.antoine.mydataanalyser.facebook.parsers

import android.util.JsonReader
import com.mandin.antoine.mydataanalyser.facebook.CharsetsUtils
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

abstract class Parser<T> {
    @Throws(IOException::class)
    fun readJson(input: InputStream): T {
        val reader = JsonReader(InputStreamReader(input, Charsets.UTF_8))
        return reader.use {
            readWhole(reader)
        }
    }

    abstract fun readWhole(reader: JsonReader): T


    protected final fun nextString(reader: JsonReader): String {
        return CharsetsUtils.translateIsoToUtf(reader.nextString())
    }
}