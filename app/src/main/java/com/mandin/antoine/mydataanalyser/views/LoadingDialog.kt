package com.mandin.antoine.mydataanalyser.views

import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.ProgressBar
import com.mandin.antoine.mydataanalyser.R
import com.mandin.antoine.mydataanalyser.tools.TaskObserver
import kotlinx.android.synthetic.main.dialog_loading.*

/**
 * Dialog used while executing a long task
 *
 * TODO("Enhancement") Add the possibility to run in background
 * @see TaskObserver
 * @see Dialog
 * @see R.layout.dialog_loading
 */
class LoadingDialog(context: Context) : Dialog(context) {
    val observer: TaskObserver = object : TaskObserver {
        override fun notify(message: String) {
            tvInfo.post {
                tvInfo.text = message
            }
        }

        override fun notifyProgress(progress: Int) {
            progressBar.post {
                progressBar.progress = progress
            }
        }

        override fun setMaxProgress(maxProgress: Int) {
            progressBar.post {
                this@LoadingDialog.maxProgress = maxProgress
            }
        }
    }
    private var progressBar: ProgressBar

    init {
        setContentView(R.layout.dialog_loading)
        setCancelable(false)
        progressBar = findViewById(R.id.progressBarHorizontal)
    }

    /**
     * Does the dialog show a progress bar
     */
    var hasProgress: Boolean
        get() = progressBar.visibility == View.VISIBLE
        set(visible) {
            if (visible)
                progressBar.visibility = View.VISIBLE
            else
                progressBar.visibility = View.GONE
        }

    /**
     * The max progress of the dialog's progress bar
     */
    var maxProgress: Int
        get() = progressBar.max
        set(value) {
            progressBar.max = value
        }
}