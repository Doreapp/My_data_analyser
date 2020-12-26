package com.mandin.antoine.mydataanalyser.facebook.asynctasks

import android.content.Context
import androidx.documentfile.provider.DocumentFile
import com.mandin.antoine.mydataanalyser.facebook.Paths
import com.mandin.antoine.mydataanalyser.facebook.model.data.PostsData
import com.mandin.antoine.mydataanalyser.facebook.model.data.PostsStats
import com.mandin.antoine.mydataanalyser.facebook.parsers.PostsParser
import com.mandin.antoine.mydataanalyser.tools.TaskObserver
import com.mandin.antoine.mydataanalyser.utils.Debug
import java.util.concurrent.Callable

/**
 * Async task for loading posts data
 */
class LoadPostsTask(
    private val docFile: DocumentFile,
    private val context: Context,
    private val observer: TaskObserver?
) : Callable<LoadPostsTask.Result> {
    private val TAG = "LoadPostsTask"
    private val result = Result()

    override fun call(): LoadPostsTask.Result {
        Debug.i(TAG, "<call>")
        observer?.notify("Loading... [Posts]")
        docFile.findFile(Paths.Posts.PATH)?.listFiles()?.let {
            buildPostsData(it)?.let { postsData ->
                result.postsData = postsData
                result.postsStats = buildPostsStats(postsData)
            }
        }
        return result
    }

    /**
     * Build posts from a list of files
     *
     * @see PostsParser
     */
    private fun buildPostsData(files: Array<DocumentFile>): PostsData? {
        Debug.i(TAG, "buildPostsData (${files.size} files)")
        observer?.setMaxProgress(files.size)

        val parser = PostsParser()
        var postsData: PostsData? = null
        var progress = 0
        for (child in files) {
            if (child.isFile && child.name?.startsWith(Paths.Posts.YOUR_POSTS_X) == true) {
                context.contentResolver.openInputStream(child.uri)?.let {
                    postsData = parser.readJson(it)
                }
            }
            progress++
            observer?.notifyProgress(progress)
        }
        return postsData
    }

    private fun buildPostsStats(postsData: PostsData): PostsStats? {
        observer?.notify("Building statistics...")
        return PostsStats(postsData)
    }

    inner class Result(
        var postsData: PostsData? = null,
        var postsStats: PostsStats? = null
    )

}