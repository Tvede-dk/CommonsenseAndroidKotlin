package com.commonsense.android.kotlin.base.scheduling

import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import kotlinx.coroutines.experimental.sync.Mutex
import kotlinx.coroutines.experimental.sync.withLock
import java.lang.ref.WeakReference
import kotlin.coroutines.experimental.CoroutineContext

/**
 * Created by Kasper Tvede on 22-06-2017.
 */


class JobContainer {

    private val localJobMutex = Mutex()

    private val groupJobMutex = Mutex()

    private val localJobs = mutableListOf<WeakReference<Job>>()

    private val groupedJobs = HashMap<String, WeakReference<Job>>()

    //<editor-fold desc="Add job ">
    private fun addJobToLocal(job: Job) {
        changeLocalJob { localJobs.add(WeakReference(job)) }
        handleCompletedCompletion(job)
    }

    private fun addJobToGroup(job: Job, group: String) {
        changeGroupJob {
            this[group]?.get()?.cancel()
            this[group] = WeakReference(job)
        }
        handleCompletedCompletion(job)
    }
    //</editor-fold>

    private fun handleCompletedCompletion(job: Job) {
        job.invokeOnCompletion {
            if (groupJobMutex.isLocked || localJobMutex.isLocked) {
                return@invokeOnCompletion
            }
            removeDoneJobs()
        }
    }

    //<editor-fold desc="Description">
    fun removeDoneJobs() {
        changeLocalJob { removeAll { it.get()?.isCompleted ?: true } }
        changeGroupJob {
            this.filter { it.value.get() == null }
                    .forEach { remove(it.key) }
        }
    }


    fun cleanJobs() {
        localJobs.forEach { it.get()?.cancel() }
        groupedJobs.forEach { it.value.get()?.cancel() }
    }
    //</editor-fold>


    //<editor-fold desc="regular non grouped Actions">
    fun addJob(job: Job) {
        addJobToLocal(job)
    }

    fun performAction(context: CoroutineContext, action: suspend () -> Unit): Job {
        val job = launch(context) { action() }
        addJobToLocal(job)
        return job
    }

    fun performAction(context: CoroutineContext, scopedAction: suspend CoroutineScope.() -> Unit): Job {
        val job = launch(context, block = scopedAction)
        addJobToLocal(job)
        return job
    }
    //</editor-fold>

    //<editor-fold desc="get jobs">
    fun getRemainingJobs(): Int {
        removeDoneJobs()
        return localJobs.size
    }

    fun getRemainingGroupedJobs(): Int {
        removeDoneJobs()
        return groupedJobs.size
    }
    //</editor-fold>

    //<editor-fold desc="group action">

    fun addJob(job: Job, group: String) {
        addJobToGroup(job, group)
    }

    fun performAction(context: CoroutineContext,
                      scopedAction: suspend CoroutineScope.() -> Unit,
                      forGroup: String): Job {
        val job = launch(context, block = scopedAction)
        addJobToGroup(job, forGroup)
        return job
    }

    fun performAction(context: CoroutineContext, action: suspend () -> Unit, forGroup: String): Job {
        val job = launch(context) { action() }
        addJobToGroup(job, forGroup)
        return job
    }
    //</editor-fold>

    //<editor-fold desc="Inline mutex functions">
    private inline fun changeLocalJob(crossinline action: MutableList<WeakReference<Job>>.() -> Unit) = runBlocking {
        localJobMutex.withLock {
            action(localJobs)
        }
    }

    private inline fun changeGroupJob(crossinline action: HashMap<String, WeakReference<Job>>.() -> Unit)
            = runBlocking {
        groupJobMutex.withLock {
            action(groupedJobs)
        }
    }
    //</editor-fold>
}