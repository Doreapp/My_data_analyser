package com.mandin.antoine.mydataanalyser.tools

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Callable
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Runner allowing to execute a task async
 *
 * @see Executor
 * @see Executors
 * @see Handler
 * @see Callable
 */
class TaskRunner {
    private val executor: Executor = Executors.newSingleThreadExecutor() // change according to your requirements
    private val handler: Handler = Handler(Looper.getMainLooper())

    /**
     * Callback for a task, called at its end
     */
    interface Callback<R> {
        fun onComplete(result: R)
    }

    /**
     * Launch a task
     */
    fun <R> executeAsync(callable: Callable<R>, callback: Callback<R>) {
        executor.execute {
            val result: R = callable.call()
            handler.post { callback.onComplete(result) }
        }
    }
}