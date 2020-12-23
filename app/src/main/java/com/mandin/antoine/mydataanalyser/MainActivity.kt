package com.mandin.antoine.mydataanalyser

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.documentfile.provider.DocumentFile
import com.mandin.antoine.mydataanalyser.facebook.asynctasks.ExploreFacebookTask
import com.mandin.antoine.mydataanalyser.facebook.asynctasks.LoadDatabaseTask
import com.mandin.antoine.mydataanalyser.facebook.database.FacebookDbHelper
import com.mandin.antoine.mydataanalyser.facebook.model.data.FacebookData
import com.mandin.antoine.mydataanalyser.tools.TaskRunner
import com.mandin.antoine.mydataanalyser.utils.Constants
import com.mandin.antoine.mydataanalyser.utils.Debug
import com.mandin.antoine.mydataanalyser.views.LoadingDialog
import com.mandin.antoine.mydataanalyser.views.facebook.ConversationsAdapter
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
            with(LoadingDialog(this)) {
                hasProgress = true
                TaskRunner().executeAsync(
                    ExploreFacebookTask(doc, this@MainActivity, observer),
                    object : TaskRunner.Callback<FacebookData?> {
                        override fun onComplete(result: FacebookData?) {
                            result?.let { res ->
                                showFacebookData(res)
                            }
                            dismiss()
                        }
                    })
                show()
            }
        }
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
                LoadDatabaseTask(this@MainActivity, observer),
                object : TaskRunner.Callback<FacebookData?> {
                    override fun onComplete(result: FacebookData?) {
                        Debug.i(TAG, "load database data result : $result")
                        result?.let { res ->
                            showFacebookData(res)
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
    fun showFacebookData(facebookData: FacebookData) {
        Debug.i(TAG, "show facebook data : $facebookData")

        Debug.i(TAG, "total message count : ${facebookData.conversationBoxData?.inbox?.size}")

        //recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ConversationsAdapter(facebookData.conversationBoxData?.inbox)
    }

}