package com.mandin.antoine.mydataanalyser

import android.net.Uri
import android.os.Bundle
import androidx.documentfile.provider.DocumentFile
import com.mandin.antoine.mydataanalyser.facebook.asynctasks.LoadConversationsTask
import com.mandin.antoine.mydataanalyser.facebook.asynctasks.LoadDatabaseTask
import com.mandin.antoine.mydataanalyser.facebook.database.FacebookDbHelper
import com.mandin.antoine.mydataanalyser.facebook.model.data.ConversationBoxData
import com.mandin.antoine.mydataanalyser.tools.TaskRunner
import com.mandin.antoine.mydataanalyser.utils.Debug
import com.mandin.antoine.mydataanalyser.utils.Preferences
import com.mandin.antoine.mydataanalyser.views.LoadingDialog
import com.mandin.antoine.mydataanalyser.views.facebook.ConversationsAdapter
import kotlinx.android.synthetic.main.activity_conversations.*


class ConversationsActivity : BaseActivity() {
    val TAG: String = "ConversationsActivity"
    private var facebookDbHelper: FacebookDbHelper? = null

    /**
     * on activity create
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        Debug.i(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversations)

        loadDatabaseData()
    }

    override fun onDestroy() {
        facebookDbHelper?.close()
        super.onDestroy()
    }

    /**
     * Try to load the information stored in the database
     *
     * @see FacebookDbHelper
     */
    fun loadDatabaseData() {
        with(LoadingDialog(this)) {
            hasProgress = true

            TaskRunner().executeAsync(
                LoadDatabaseTask(this@ConversationsActivity, observer),
                object : TaskRunner.Callback<ConversationBoxData?> {
                    override fun onComplete(result: ConversationBoxData?) {
                        Debug.i(TAG, "load database data result : $result")
                        when (result) {
                            null -> loadFromStorage()
                            else -> showConversationData(result)
                        }
                        dismiss()
                    }
                })
            show()
        }
    }

    /**
     * Show a facebook data information
     *
     * Such as conversations list
     */
    fun showConversationData(conversationBoxData: ConversationBoxData) {
        Debug.i(TAG, "show conversationBoxData : $conversationBoxData")

        Debug.i(TAG, "total message count : ${conversationBoxData.inbox?.size}")

        //recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ConversationsAdapter(conversationBoxData.inbox)
    }

    /**
     * Load conversations data from the storage
     *
     * @see Preferences.getFacebookFolderUri
     */
    fun loadFromStorage() {
        val folderUri = Uri.parse(Preferences.getFacebookFolderUri(this))
        if (folderUri != null) {
            val docFile = DocumentFile.fromTreeUri(this, folderUri)
            if (docFile != null) {
                with(LoadingDialog(this)) {
                    hasProgress = true
                    TaskRunner().executeAsync(
                        LoadConversationsTask(docFile, this@ConversationsActivity, observer),
                        object : TaskRunner.Callback<ConversationBoxData?> {
                            override fun onComplete(result: ConversationBoxData?) {
                                result?.let { res ->
                                    showConversationData(res)
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