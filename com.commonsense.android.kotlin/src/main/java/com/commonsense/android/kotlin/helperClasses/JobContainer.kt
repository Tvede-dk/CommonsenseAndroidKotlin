package com.commonsense.android.kotlin.helperClasses

import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import kotlinx.coroutines.experimental.sync.Mutex
import java.lang.ref.WeakReference
import kotlin.coroutines.experimental.CoroutineContext

/**
 * Created by Kasper Tvede on 22-06-2017.
 */


class JobContainer {

    private val mutex = Mutex()

    private val localJobs = mutableListOf<WeakReference<Job>>()

    private fun addJobToLocal(job: Job) {
        changeList { localJobs.add(WeakReference(job)) }
        job.invokeOnCompletion {
            removeDoneJobs()
        }
    }

    fun removeDoneJobs() = changeList {
        this.localJobs.removeAll { it.get()?.isCompleted ?: true }
    }

    private inline fun changeList(crossinline action: () -> Unit) = runBlocking {
        mutex.lock()
        try {
            action()
        } finally {
            mutex.unlock()
        }
    }

    fun cleanJobs() = changeList {
        localJobs.forEach { it.get()?.cancel() }
        localJobs.clear()
    }

    fun performAction(context: CoroutineContext, action: suspend () -> Unit) {
        addJobToLocal(launch(context) { action() })
    }

    fun performAction(context: CoroutineContext, scopedAction: suspend CoroutineScope.() -> Unit) {
        addJobToLocal(launch(context, block = scopedAction))
    }

    fun getRemainingJobs(): Int {
        removeDoneJobs()
        return localJobs.size
    }
}