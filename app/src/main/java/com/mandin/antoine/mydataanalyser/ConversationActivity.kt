package com.mandin.antoine.mydataanalyser

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import com.mandin.antoine.mydataanalyser.facebook.MessagesParser
import com.mandin.antoine.mydataanalyser.facebook.database.FacebookDbHelper
import com.mandin.antoine.mydataanalyser.facebook.model.Conversation
import com.mandin.antoine.mydataanalyser.facebook.model.Person
import com.mandin.antoine.mydataanalyser.facebook.model.data.ConversationData
import com.mandin.antoine.mydataanalyser.facebook.model.data.ConversationStats
import com.mandin.antoine.mydataanalyser.utils.Constants
import com.mandin.antoine.mydataanalyser.utils.Debug
import com.mandin.antoine.mydataanalyser.views.NumberedItemAdapter
import kotlinx.android.synthetic.main.view_conversation.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Activity Showing conversation information
 */
class ConversationActivity : AppCompatActivity() {
    private val TAG = "ConversationActivity"
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
    private var conversationData: ConversationData? = null
    private var conversation: Conversation? = null
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

                dbHelper.findConversationById(id as Long)?.let { findConversation(it) }
            }
        }
    }

    /**
     * Find a [Conversation] from a [ConversationData].
     * Browse in files
     *
     * TODO do this in an async task
     *
     * @see MessagesParser
     */
    private fun findConversation(conversationData: ConversationData) {
        this.conversationData = conversationData
        Debug.i(TAG, "findConversation. conversationData=$conversationData")
        conversationData.uri?.let { uri ->
            DocumentFile.fromTreeUri(this, uri)?.let { folder ->
                val parser = MessagesParser(dbHelper)
                for (child in folder.listFiles()) {
                    if (child.isFile) {
                        contentResolver.openInputStream(child.uri)?.let {
                            conversation = parser.readJson(it)
                        }
                    }
                }
            }
            updateDisplay()
        }
    }

    /**
     * Update the display of the activity
     */
    fun updateDisplay() {
        Debug.i(TAG, "updateDisplay.")
        conversationData?.let { data ->
            tvConversationTitle.text = data.title
            tvConversationStatistics.text = "Started the ${dateFormat.format(data.creationDate)}\n" +
                    "${data.messageCount} messages sent\n" +
                    "${data.audioCount} audios sent\n" +
                    "${data.photoCount} photos sent\n" +
                    "${data.gifCount} gif sent"
        }

        conversation?.let { conversation ->
            val statistics = ConversationStats(conversation)
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

            listMembers.adapter = adapter
            listMembers.isShowMoreButtonVisible = true
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        dbHelper.close()
    }
}