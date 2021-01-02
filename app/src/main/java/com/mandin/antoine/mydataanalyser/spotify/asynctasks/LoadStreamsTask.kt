package com.mandin.antoine.mydataanalyser.spotify.asynctasks

import android.content.Context
import androidx.documentfile.provider.DocumentFile
import com.mandin.antoine.mydataanalyser.spotify.Paths
import com.mandin.antoine.mydataanalyser.spotify.model.data.StreamsData
import com.mandin.antoine.mydataanalyser.spotify.model.data.StreamsStats
import com.mandin.antoine.mydataanalyser.spotify.parsers.StreamsParser
import com.mandin.antoine.mydataanalyser.tools.TaskObserver
import com.mandin.antoine.mydataanalyser.utils.Debug
import java.util.concurrent.Callable

/**
 * Async task used to load Spotify streams data
 *
 * @see StreamsData
 * @see StreamsStats
 */
class LoadStreamsTask(
    private val docFile: DocumentFile,
    private val context: Context,
    private val observer: TaskObserver?
) : Callable<LoadStreamsTask.Result> {
    private val TAG = "LoadStreamsTask"
    private val result = Result()

    override fun call(): Result {
        Debug.i(TAG, "<call>")
        observer?.notify("Loading... [Streams]")
        docFile.listFiles().let {
            buildStreamsData(it)?.let { streamsData ->
                result.streamsData = streamsData
                observer?.notify("Building stats...")
                result.streamsStats = StreamsStats(streamsData)
            }
        }
        return result
    }

    /**
     * Build [StreamsData] from an array of files
     */
    private fun buildStreamsData(files: Array<DocumentFile>): StreamsData? {
        Debug.i(TAG, "buildStreamsData (${files.size} files)")
        observer?.setMaxProgress(files.size)

        val parser = StreamsParser()
        var streamsData: StreamsData? = null
        var progress = 0
        for (child in files) {
            if (child.isFile && child.name?.startsWith(Paths.STREAMING_HISTORY_X) == true) {
                context.contentResolver.openInputStream(child.uri)?.let {
                    streamsData = parser.readJson(it)
                }
            }
            progress++
            observer?.notifyProgress(progress)
        }
        return streamsData
    }

    /**
     * Result class containing [StreamsData] and [StreamsStats]
     */
    inner class Result(
        var streamsData: StreamsData? = null,
        var streamsStats: StreamsStats? = null
    )

}