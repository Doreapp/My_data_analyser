package com.mandin.antoine.mydataanalyser

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.mandin.antoine.mydataanalyser.facebook.asynctasks.LoadConversationStatsTask
import com.mandin.antoine.mydataanalyser.facebook.database.FacebookDbHelper
import com.mandin.antoine.mydataanalyser.facebook.model.Conversation
import com.mandin.antoine.mydataanalyser.facebook.model.Person
import com.mandin.antoine.mydataanalyser.facebook.model.data.ConversationData
import com.mandin.antoine.mydataanalyser.facebook.model.data.ConversationStats
import com.mandin.antoine.mydataanalyser.tools.TaskRunner
import com.mandin.antoine.mydataanalyser.utils.Constants
import com.mandin.antoine.mydataanalyser.utils.Debug
import com.mandin.antoine.mydataanalyser.views.LoadingDialog
import com.mandin.antoine.mydataanalyser.views.adapters.NumberedItemAdapter
import kotlinx.android.synthetic.main.view_conversation.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Activity Showing conversation information
 */
class ConversationActivity : BaseActivity() {
    private val TAG = "ConversationActivity"
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
    private val dbHelper = FacebookDbHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_conversation)
        Debug.i(TAG, "on create ")

        readIntent()
    }

    /**
     * Read the launcher intent
     * Must contain extra [Constants.EXTRA_CONVERSATION_ID] with the id of the conversation
     *
     */
    private fun readIntent() {
        Debug.i(TAG, "readIntent")
        intent?.let { intent ->
            val conversationId = intent.extras?.get(Constants.EXTRA_CONVERSATION_ID)
            conversationId?.let { id ->
                readConversationValues(id as Long)
            }
        }
    }

    /**
     * Read the values of a conversation with and async tack
     *
     * @param id Id of the conversation to read
     * @see LoadConversationStatsTask
     */
    private fun readConversationValues(id: Long) {
        with(LoadingDialog(this)) {
            hasProgress = true
            TaskRunner().executeAsync(
                LoadConversationStatsTask(
                    this@ConversationActivity, dbHelper,
                    id, observer
                ), object : TaskRunner.Callback<LoadConversationStatsTask.Result> {
                    override fun onComplete(result: LoadConversationStatsTask.Result) {
                        displayConversation(
                            result.conversationData,
                            result.conversation,
                            result.conversationStats
                        )

                        result.photoFolderUri?.let { uri ->
                            setPhotoFolderUri(uri)
                        }
                        dismiss()
                    }
                })

            show()
        }
    }

    /**
     * Update the display of the activity
     */
    fun displayConversation(
        conversationData: ConversationData?,
        conversation: Conversation?, conversationStats: ConversationStats?
    ) {
        conversationData?.let { data ->
            tvConversationTitle.text = data.title
            tvConversationStatistics.text = "Started the ${dateFormat.format(data.creationDate)}\n" +
                    "${data.messageCount} messages sent\n" +
                    "${data.audioCount} audios sent\n" +
                    "${data.photoCount} photos sent\n" +
                    "${data.gifCount} gif sent"
        }

        conversationStats?.let { statistics ->
            val adapter = object : NumberedItemAdapter<MutableMap.MutableEntry<Person, Int>>(
                ArrayList(statistics.participantsMessageCount.entries)
            ) {
                override fun getName(value: MutableMap.MutableEntry<Person, Int>): String? {
                    return value.key.name
                }

                override fun getNumber(value: MutableMap.MutableEntry<Person, Int>): Int {
                    return value.value
                }
            }
            adapter.showPercentage = true

            listMembers.adapter = adapter
            listMembers.isShowMoreButtonVisible = true

            Debug.i(
                TAG, "stats on message par period : " +
                        "\nper year : ${statistics.messageCountByYear.entries.toTypedArray().contentToString()} " +
                        "\nper month : ${statistics.messageCountByMonth.entries.toTypedArray().contentToString()} " +
                        "\nper week : ${statistics.messageCountByWeek.entries.toTypedArray().contentToString()} "
            )

            periodLineChart.countsYearly = statistics.messageCountByYear
            periodLineChart.countsMonthly = statistics.messageCountByMonth
            periodLineChart.countsWeekly = statistics.messageCountByWeek
            periodLineChart.lineLabel = "message count"

            periodLineChart.showCountsYearly()
        }
    }

    /**
     * Set the uri of the photo folder
     *
     * @param uri Uri of the folder
     */
    fun setPhotoFolderUri(uri: Uri) {
        btnShowPhotos.setOnClickListener {
            val intent = Intent(this, GalleryActivity::class.java)
            intent.putExtra(Constants.EXTRA_PHOTO_FOLDER_URI, uri.toString())
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        dbHelper.close()
    }
}