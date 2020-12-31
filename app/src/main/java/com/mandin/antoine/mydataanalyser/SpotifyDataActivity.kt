package com.mandin.antoine.mydataanalyser

import android.net.Uri
import android.os.Bundle
import androidx.documentfile.provider.DocumentFile
import com.mandin.antoine.mydataanalyser.spotify.asynctasks.LoadStreamsTask
import com.mandin.antoine.mydataanalyser.tools.TaskRunner
import com.mandin.antoine.mydataanalyser.utils.Debug
import com.mandin.antoine.mydataanalyser.utils.Preferences
import com.mandin.antoine.mydataanalyser.views.LoadingDialog

class SpotifyDataActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spotify_data)

        val folderUri = Uri.parse(Preferences.getSpotifyFolderUri(this))
        if (folderUri != null) {
            val docFile = DocumentFile.fromTreeUri(this, folderUri)
            if (docFile != null) {
                with(LoadingDialog(this)) {
                    hasProgress = true
                    TaskRunner().executeAsync(
                        LoadStreamsTask(docFile, this@SpotifyDataActivity, observer),
                        object : TaskRunner.Callback<LoadStreamsTask.Result> {
                            override fun onComplete(result: LoadStreamsTask.Result) {
                                Debug.i("SDA", "on result : ${result.streamsData?.streams?.size} streams")
                                dismiss()
                            }
                        })
                    show()
                }
            } else {
                alert("We were not able to found the spotify folder in your storage.")
            }
        } else {
            alert("You didn't specified the spotify folder location in your storage.")
        }
    }
}