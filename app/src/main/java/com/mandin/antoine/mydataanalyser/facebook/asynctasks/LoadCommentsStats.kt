package com.mandin.antoine.mydataanalyser.facebook.asynctasks

import android.content.Context
import androidx.documentfile.provider.DocumentFile
import com.mandin.antoine.mydataanalyser.facebook.Paths
import com.mandin.antoine.mydataanalyser.facebook.model.data.CommentsData
import com.mandin.antoine.mydataanalyser.facebook.model.data.CommentsStats
import com.mandin.antoine.mydataanalyser.facebook.parsers.CommentsParser
import com.mandin.antoine.mydataanalyser.facebook.parsers.PostsParser
import com.mandin.antoine.mydataanalyser.tools.TaskObserver
import com.mandin.antoine.mydataanalyser.utils.Debug
import java.util.concurrent.Callable

class LoadCommentsStats(
    private val docFile: DocumentFile,
    private val context: Context,
    private val observer: TaskObserver?
) : Callable<LoadCommentsStats.Result> {
    private val TAG = "LoadCommentsStats"
    private val result = Result()

    override fun call(): Result {
        Debug.i(TAG, "<call>")
        observer?.notify("Loading... [Comment]")
        docFile.findFile(Paths.Comments.PATH)?.listFiles()?.let {
            buildCommentsData(it)?.let { commentsData ->
                result.commentsData = commentsData
                result.commentsStats = buildCommentsStats(commentsData)
            }
        }
        return result
    }

    /**
     * Build comments from a list of files
     *
     * @see PostsParser
     */
    private fun buildCommentsData(files: Array<DocumentFile>): CommentsData? {
        Debug.i(TAG, "buildCommentsData (${files.size} files)")
        observer?.setMaxProgress(files.size)

        val parser = CommentsParser()
        var commentsData: CommentsData? = null
        var progress = 0
        for (child in files) {
            context.contentResolver.openInputStream(child.uri)?.let {
                commentsData = parser.readJson(it)
            }

            progress++
            observer?.notifyProgress(progress)
        }
        return commentsData
    }

    private fun buildCommentsStats(commentsData: CommentsData): CommentsStats? {
        observer?.notify("Building statistics...")
        return CommentsStats(commentsData)
    }

    inner class Result(
        var commentsData: CommentsData? = null,
        var commentsStats: CommentsStats? = null
    )

}