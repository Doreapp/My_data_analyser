package com.mandin.antoine.mydataanalyser.tools

interface TaskObserver {
    fun notify(message: String)
    fun notifyProgress(progress: Int)
    fun setMaxProgress(maxProgress: Int)
}