package com.mandin.antoine.mydataanalyser

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.documentfile.provider.DocumentFile
import com.mandin.antoine.mydataanalyser.facebook.asynctasks.LoadPostsTask
import com.mandin.antoine.mydataanalyser.facebook.model.data.PostsData
import com.mandin.antoine.mydataanalyser.facebook.model.data.PostsStats
import com.mandin.antoine.mydataanalyser.tools.TaskRunner
import com.mandin.antoine.mydataanalyser.utils.Debug
import com.mandin.antoine.mydataanalyser.utils.Preferences
import com.mandin.antoine.mydataanalyser.views.LoadingDialog
import com.mandin.antoine.mydataanalyser.views.adapters.NumberedItemAdapter
import com.mandin.antoine.mydataanalyser.views.adapters.PostsAdapter
import kotlinx.android.synthetic.main.activity_posts.*

/**
 * Activity to show posts statistics
 */
class PostsActivity : BaseActivity() {
    private val TAG = "PostsActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posts)

        loadFromStorage()

        btnBrowsePosts.setOnClickListener {
            listPosts.visibility = View.VISIBLE
        }
    }

    /**
     * Show post data
     */
    fun displayData(postsData: PostsData?, postsStats: PostsStats?) {
        Debug.i(TAG, "showPostsData (${postsData?.posts?.size} posts)")
        postsData?.let { data ->
            tvPostCount.text = "${data.posts.size} posts"
            listPosts.adapter = PostsAdapter(data.posts.sortedBy {
                it.date
            })
            btnBrowsePosts.isEnabled = true
        }
        postsStats?.let { stats ->
            // Chart showing the evolution of post number by periods
            with(periodLineChart) {
                countsWeekly = stats.postCountByWeek
                countsMonthly = stats.postCountByMonth
                countsYearly = stats.postCountByYear
                lineLabel = "Post count"
                showCountsYearly()
            }
            // Number of photos
            tvPhotoCount.text = "${stats.photoCount} photos"

            // Where post were posted numbered list
            val adapter = object : NumberedItemAdapter<MutableMap.MutableEntry<String, Int>>(
                ArrayList(stats.whereCounts.entries)
            ) {
                override fun getName(value: MutableMap.MutableEntry<String, Int>): String {
                    return value.key
                }

                override fun getNumber(value: MutableMap.MutableEntry<String, Int>): Int {
                    return value.value
                }
            }
            adapter.showPercentage = true

            listWheres.adapter = adapter
            listWheres.isShowMoreButtonVisible = true

        }
    }

    /**
     * Load posts data from the storage
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
                        object : TaskRunner.Callback<LoadPostsTask.Result> {
                            override fun onComplete(result: LoadPostsTask.Result) {
                                displayData(result.postsData, result.postsStats)
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

    override fun onBackPressed() {
        if (listPosts.visibility == View.VISIBLE) {
            listPosts.visibility = View.GONE
        } else {
            super.onBackPressed()
        }
    }
}