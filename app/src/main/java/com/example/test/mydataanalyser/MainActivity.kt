package com.example.test.mydataanalyser

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.test.mydataanalyser.facebook.MessagesParser
import com.example.test.mydataanalyser.utils.Constants
import com.example.test.mydataanalyser.utils.Debug
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.view_conversation.view.*


// TODO(Comments) Comment this class and its methods
class MainActivity : AppCompatActivity() {
    val TAG: String = "MainActivity"
    var readFilePermissionDialog: AlertDialog? = null

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

        btnChoose.setOnClickListener {
            startPickFileIntent()
        }
    }

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

    private fun requestReadFilePermission() {
        Debug.i(TAG, "requestReadFilePermission()")
        requestPermissions(
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
            Constants.REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>,
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Debug.i(TAG, "onActivityResult --> requestCode=$requestCode + resultCode=$resultCode + data=$data")
        when (requestCode) {
            Constants.REQUEST_CODE_PICK_JSON_FILE -> {
                when (resultCode) {
                    RESULT_OK -> {
                        Log.i(TAG, "file selected")
                        data?.data?.let {
                            onFilePicked(it)
                        }
                    }
                }
            }
        }
    }

    fun onReadFilePermissionGranted() {
        Debug.i(TAG, "onReadFilePermissionGranted")
        readFilePermissionDialog?.dismiss()
        btnChoose.isEnabled = true
    }

    fun startPickFileIntent() {
        Debug.i(TAG, "startPickFileIntent")
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, Constants.REQUEST_CODE_PICK_JSON_FILE)
    }

    fun onFilePicked(uri: Uri) {
        Debug.i(TAG, "onFilePicked uri=$uri")

        //TODO(Feature) read file as a message file
        val parser = MessagesParser()
        try {
            contentResolver.openInputStream(uri)?.let {
                Debug.i(TAG, " onFilePicked openInputStream opened")
                val conv = parser.readJson(it)
                Debug.i(TAG, " conversation red : $conv")

                viewConversation.title.text = conv.title
                viewConversation.messageCount.text = "${conv.messages.size}"
                val msgFirst = conv.messages[0]
                var msgLast = conv.messages[conv.messages.size - 1]
                if (msgFirst.sendingDate?.before(msgLast.sendingDate) == true) {
                    msgLast = msgFirst
                }
                viewConversation.firstMessage.text = "${msgLast.person} : ${msgLast.content}"
                viewConversation.firstMessageDate.text = "${msgLast.sendingDate}"
            }
            Debug.i(TAG, "onFilePicked try end")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}