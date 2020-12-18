package com.mandin.antoine.mydataanalyser.views

import android.app.Dialog
import android.content.Context
import com.mandin.antoine.mydataanalyser.R
import com.mandin.antoine.mydataanalyser.tools.TaskNotifier
import kotlinx.android.synthetic.main.dialog_loading.*

class LoadingDialog(context: Context) : Dialog(context) {
    val notifier: TaskNotifier = object : TaskNotifier {
        override fun notify(message: String) {
            tvInfo.text = message
        }
    }

    init {
        setContentView(R.layout.dialog_loading)
        setCancelable(false)
    }

}