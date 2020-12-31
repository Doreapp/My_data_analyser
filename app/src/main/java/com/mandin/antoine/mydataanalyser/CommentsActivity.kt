package com.mandin.antoine.mydataanalyser

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.documentfile.provider.DocumentFile
import com.mandin.antoine.mydataanalyser.facebook.asynctasks.LoadCommentsStats
import com.mandin.antoine.mydataanalyser.facebook.model.data.CommentsData
import com.mandin.antoine.mydataanalyser.facebook.model.data.CommentsStats
import com.mandin.antoine.mydataanalyser.tools.TaskRunner
import com.mandin.antoine.mydataanalyser.utils.Debug
import com.mandin.antoine.mydataanalyser.utils.Preferences
import com.mandin.antoine.mydataanalyser.views.LoadingDialog
import com.mandin.antoine.mydataanalyser.views.adapters.CommentsAdapter
import com.mandin.antoine.mydataanalyser.views.adapters.NumberedItemAdapter
import kotlinx.android.synthetic.main.activity_comments.*

/**
 * Activity to display data and stats about facebook comments
 */
class CommentsActivity : BaseActivity() {
    private val TAG = "CommentsActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        loadFromStorage()

        btnBrowseComments.setOnClickListener {
            listComments.visibility = View.VISIBLE
        }
    }


    /**
     * Show comments data
     */
    fun displayData(commentsData: CommentsData?, commentsStats: CommentsStats?) {
        Debug.i(TAG, "showPostsData (${commentsData?.comments?.size} posts)")
        commentsData?.let { data ->
            tvCommentCount.text = "${data.comments.size} comments"
            listComments.adapter = CommentsAdapter(data.comments.sortedBy {
                it.date
            })
            btnBrowseComments.isEnabled = true
        }
        commentsStats?.let { stats ->
            // Chart showing the evolution of post number by periods
            with(periodLineChart) {
                countsWeekly = stats.commentCountByWeek
                countsMonthly = stats.commentCountByMonth
                countsYearly = stats.commentCountByYear
                lineLabel = "Comment count"
                showCountsYearly()
            }
            // Number of photos
            tvPhotoCount.text = "${stats.photoCount} photos"

            // Where
            val whereAdapter = object : NumberedItemAdapter<MutableMap.MutableEntry<String, Int>>(
                ArrayList(stats.whereCounts.entries)
            ) {
                override fun getName(value: MutableMap.MutableEntry<String, Int>): String {
                    return value.key
                }

                override fun getNumber(value: MutableMap.MutableEntry<String, Int>): Int {
                    return value.value
                }
            }
            whereAdapter.showPercentage = true

            listWheres.adapter = whereAdapter
            listWheres.isShowMoreButtonVisible = true


            // What groups
            val groupsAdapter = object : NumberedItemAdapter<MutableMap.MutableEntry<String, Int>>(
                ArrayList(stats.groupCounts.entries)
            ) {
                override fun getName(value: MutableMap.MutableEntry<String, Int>): String {
                    return value.key
                }

                override fun getNumber(value: MutableMap.MutableEntry<String, Int>): Int {
                    return value.value
                }
            }
            groupsAdapter.showPercentage = true

            listGroups.adapter = groupsAdapter
            listGroups.isShowMoreButtonVisible = true

        }
    }

    /**
     * Load comments data from the storage
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
                        LoadCommentsStats(docFile, this@CommentsActivity, observer),
                        object : TaskRunner.Callback<LoadCommentsStats.Result> {
                            override fun onComplete(result: LoadCommentsStats.Result) {
                                displayData(result.commentsData, result.commentsStats)
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
        if (listComments.visibility == View.VISIBLE) {
            listComments.visibility = View.GONE
        } else {
            super.onBackPressed()
        }
    }
}