package com.mandin.antoine.mydataanalyser.tools

/**
 * Observer of an async task
 */
interface TaskObserver {
    /**
     * Notify the observer of a change in the task.
     *
     * **Warning** : May be called in a non-UI thread
     */
    fun notify(message: String)

    /**
     * Notify the observer of a change in the task progress
     *
     * **Warning** : May be called in a non-UI thread
     */
    fun notifyProgress(progress: Int)

    /**
     * Set the max progress of the task
     *
     * **Warning** : May be called in a non-UI thread
     */
    fun setMaxProgress(maxProgress: Int)
}