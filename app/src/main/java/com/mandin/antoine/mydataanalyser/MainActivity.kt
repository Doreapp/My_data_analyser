package com.mandin.antoine.mydataanalyser

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mandin.antoine.mydataanalyser.facebook.ExploreFacebookTask
import com.mandin.antoine.mydataanalyser.facebook.database.FacebookDbHelper
import com.mandin.antoine.mydataanalyser.facebook.database.LoadDatabaseTask
import com.mandin.antoine.mydataanalyser.facebook.model.data.ConversationData
import com.mandin.antoine.mydataanalyser.facebook.model.data.FacebookData
import com.mandin.antoine.mydataanalyser.tools.TaskRunner
import com.mandin.antoine.mydataanalyser.utils.Constants
import com.mandin.antoine.mydataanalyser.utils.Debug
import com.mandin.antoine.mydataanalyser.views.LoadingDialog
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    val TAG: String = "MainActivity"
    var readFilePermissionDialog: AlertDialog? = null
    private var facebookDbHelper: FacebookDbHelper? = null

    /**
     * on activity create
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        Debug.i(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
                    == PackageManager.PERMISSION_GRANTED ->
                onReadFilePermissionGranted()

            shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) ->
                openReadFilePermissionDialog()

            else -> requestReadFilePermission()
        }

        btnChooseFacebookFolder.setOnClickListener {
            startPickFolderIntent(Constants.REQUEST_CODE_PICK_FACEBOOK_FOLDER)
        }

        loadDatabaseData()
    }

    /**
     * Open a `Dialog` that ask the user to grant the *read external storage* permission
     * @see AlertDialog
     * @see Manifest.permission.READ_EXTERNAL_STORAGE
     */
    fun openReadFilePermissionDialog() {
        Debug.i(TAG, "openReadFilePermissionDialog()")
        readFilePermissionDialog =
            AlertDialog.Builder(this)
                .setMessage(R.string.need_file_permission)
                .setTitle(R.string.title_need_file_permission)
                .setCancelable(false)
                .setNegativeButton(R.string.refuse) { dialog, _ ->
                    dialog.cancel()
                    finish()
                }
                .setPositiveButton(R.string.grant) { _, _ ->
                    requestReadFilePermission()
                }
                .show()
    }

    /**
     * Request *read external storage* permission
     *
     * @see Constants.REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSION
     * @see Manifest.permission.READ_EXTERNAL_STORAGE
     */
    private fun requestReadFilePermission() {
        Debug.i(TAG, "requestReadFilePermission()")
        requestPermissions(
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
            Constants.REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSION
        )
    }

    /**
     * On request permission result
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Debug.i(
            TAG,
            "onRequestPermissionsResult() --> requestCode=$requestCode + permissions=$permissions + grantResults=$grantResults"
        )
        when (requestCode) {
            Constants.REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSION -> {
                onReadFilePermissionGranted()
            }
        }
    }

    /**
     * On activity Result
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Debug.i(TAG, "onActivityResult --> requestCode=$requestCode + resultCode=$resultCode + data=$data")
        when (requestCode) {
            Constants.REQUEST_CODE_PICK_FACEBOOK_FOLDER -> {
                when (resultCode) {
                    RESULT_OK -> {
                        Log.i(TAG, "file selected")
                        data?.data?.let {
                            onFacebookFolderPicked(it)
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        facebookDbHelper?.close()
        super.onDestroy()
    }

    /**
     * On *read external storage* permission granted
     * Enable the button
     * @see btnChooseFacebookFolder
     */
    fun onReadFilePermissionGranted() {
        Debug.i(TAG, "onReadFilePermissionGranted")
        readFilePermissionDialog?.dismiss()
        btnChooseFacebookFolder.isEnabled = true
    }

    /**
     * Start an intent whanting to choose a floder
     * @param requestCode request code corresponding to the folder to choose
     * @see Constants.REQUEST_CODE_PICK_FACEBOOK_FOLDER
     * @see Intent.ACTION_OPEN_DOCUMENT_TREE
     */
    fun startPickFolderIntent(requestCode: Int) {
        Debug.i(TAG, "startPickFileIntent")
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)

        startActivityForResult(intent, requestCode)
    }

    /**
     * Called when the user choose the folder corresponding to facebook data
     * @param uri Uri corresponding to picked folder
     */
    fun onFacebookFolderPicked(uri: Uri) {
        Debug.i(TAG, "onFacebookFolderPicked uri=$uri")

        val docFile = DocumentFile.fromTreeUri(this, uri)
        docFile?.let { doc ->
            val dialog = LoadingDialog(this)
            TaskRunner().executeAsync(
                ExploreFacebookTask(doc, this, dialog.notifier),
                object : TaskRunner.Callback<FacebookData?> {
                    override fun onComplete(result: FacebookData?) {
                        result?.let { res ->
                            showFacebookData(res)
                        }
                        dialog.dismiss()
                    }
                })
            dialog.show()
        }
    }

    fun loadDatabaseData() {
        val dialog = LoadingDialog(this)
        TaskRunner().executeAsync(
            LoadDatabaseTask(this, dialog.notifier),
            object : TaskRunner.Callback<FacebookData?> {
                override fun onComplete(result: FacebookData?) {
                    Debug.i(TAG, "load database data result : $result")
                    result?.let { res ->
                        showFacebookData(res)
                    }
                    dialog.dismiss()
                }
            })
        dialog.show()
    }

    fun showFacebookData(facebookData: FacebookData) {
        Debug.i(TAG, "show facebook data : $facebookData")

        Debug.i(TAG, "total message count : ${facebookData.conversationBoxData?.inbox?.size}")

        facebookData.conversationBoxData?.inbox =
            facebookData.conversationBoxData?.inbox?.sortedWith { c1, c2 ->
                c2.messageCount.compareTo(c1.messageCount)
            }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = Adapter(facebookData.conversationBoxData?.inbox)
    }

    inner class Adapter(private val conversations: List<ConversationData>?) :
        RecyclerView.Adapter<MainActivity.ViewHolder>() {

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): MainActivity.ViewHolder {
            return ViewHolder(
                LayoutInflater.from(this@MainActivity)
                    .inflate(R.layout.item_view_conversation, parent, false)
            )
        }

        override fun onBindViewHolder(
            holder: MainActivity.ViewHolder,
            position: Int
        ) {
            val conv = conversations?.get(position)
            holder.itemView.findViewById<TextView>(R.id.tvTitle).text = conv?.title

            holder.itemView.findViewById<TextView>(R.id.tvStats).text =
                "${conv?.messageCount} messages - Creating on ${conv?.creationDate}"

            holder.itemView.setOnClickListener { view ->
                Debug.i(TAG, "on click on $conv")
                conv?.uri?.let { uri ->
                    val file = DocumentFile.fromTreeUri(this@MainActivity, uri)
                    Debug.i(TAG, "file $file. Exists? ${file?.exists()}")
                }
            }
        }

        override fun getItemCount(): Int {
            conversations?.let { return it.size }
            return 0
        }

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}