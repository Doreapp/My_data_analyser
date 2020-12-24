package com.mandin.antoine.mydataanalyser

import android.net.Uri
import android.os.Bundle
import androidx.documentfile.provider.DocumentFile
import com.mandin.antoine.mydataanalyser.facebook.asynctasks.LoadPostsTask
import com.mandin.antoine.mydataanalyser.facebook.model.data.PostsData
import com.mandin.antoine.mydataanalyser.tools.TaskRunner
import com.mandin.antoine.mydataanalyser.utils.Debug
import com.mandin.antoine.mydataanalyser.utils.Preferences
import com.mandin.antoine.mydataanalyser.views.LoadingDialog

/**
 * Activity to show posts statistics
 */
class PostsActivity : BaseActivity() {
    private val TAG = "PostsActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posts)

        loadFromStorage()
    }

    /**
     * Show post data : TODO display
     */
    fun showPostsData(postsData: PostsData) {
        Debug.i(TAG, "showPostsData (${postsData.posts.size} posts)")
    }

    /**
     * Load conversations data from the storage
     *
     * @see Preferences.getFacebookFolderUri
     */
    fun loadFromStorage() {
        Debug.i(TAG, "loadFromStorage")
        val folderUri = Uri.parse(Preferences.getFacebookFolderUri(this))
        if (folderUri != null) {
            val docFile = DocumentFile.fromTreeUri(this, folderUri)
            if (docFile != null) {
                with(LoadingDialog(this)) {
                    hasProgress = true
                    TaskRunner().executeAsync(
                        LoadPostsTask(docFile, this@PostsActivity, observer),
                        object : TaskRunner.Callback<PostsData?> {
                            override fun onComplete(result: PostsData?) {
                                result?.let { res ->
                                    showPostsData(res)
                                }
                                dismiss()
                            }
                        })
                    show()
                }
            } else {
                alert("We were not able to found the facebook folder in your storage.")
            }
        } else {
            alert("You didn't specified the facebook folder location in your storage.")
        }
    }
}