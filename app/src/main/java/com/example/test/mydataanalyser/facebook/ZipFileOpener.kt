package com.example.test.mydataanalyser.facebook

import com.example.test.mydataanalyser.utils.Debug
import java.io.BufferedInputStream
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

class ZipFileOpener {
    val TAG = "ZipFileOpener"

    constructor(inputStream: InputStream) {
        Debug.i(TAG, "<init>")

        val zipInputStream = ZipInputStream(BufferedInputStream(inputStream))
        var zipEntry: ZipEntry?

        while (zipInputStream.nextEntry.also { zipEntry = it } != null) {
            val filename = zipEntry?.name

            // Need to create directories if not exists, or
            // it will generate an Exception...
            if (zipEntry?.isDirectory == true) {
                Debug.i(TAG, "Directory in zip : $filename")
            } else {
                Debug.i(TAG, "File in zip : $filename")
            }
            zipInputStream.closeEntry()
        }
    }
}