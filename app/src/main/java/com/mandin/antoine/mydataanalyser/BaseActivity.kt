package com.mandin.antoine.mydataanalyser

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

/**
 * Base activity
 * Contains useful functions
 */
abstract class BaseActivity : AppCompatActivity() {
    fun alert(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Warning")
            .setMessage(message)
            .setNeutralButton(android.R.string.ok, null)
            .show()
    }
}