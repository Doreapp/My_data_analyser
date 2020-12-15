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
            zipEntry?.let {
                val filename = it.name

                if (it.isDirectory) {
                    Debug.i(TAG, "Directory in zip : $filename")
                } else {
                    Debug.i(TAG, "File in zip : $filename")
                }
            }
            zipInputStream.closeEntry()
        }
    }

    private fun readFile(fileName: String, entry: ZipEntry) {
        when {
            fileName.startsWith("message_") -> {
                readMessagesFile(entry)
            }
        }
    }

    private fun readMessagesFile(entry: ZipEntry) {
        //TODO
    }
}