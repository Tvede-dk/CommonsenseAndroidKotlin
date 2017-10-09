package com.commonsense.android.kotlin.base.scheduling

import com.commonsense.android.kotlin.base.AsyncCoroutineFunction
import com.commonsense.android.kotlin.base.AsyncEmptyFunction
import com.commonsense.android.kotlin.base.extensions.use
import com.commonsense.android.kotlin.base.extensions.weakReference
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
typealias WeakJob = WeakReference<Job>

typealias WeakQueuedJob = WeakReference<Pair<CoroutineContext, AsyncEmptyFunction>>

open class JobContainer {

    private val localJobMutex = Mutex()

    private val groupJobMutex = Mutex()

    private val queuedGroupedJobsMutex = Mutex()

    private val localJobs = mutableListOf<WeakJob>()

    private val queuedGroupedJobs = hashMapOf<String, MutableList<WeakQueuedJob>>()

    private val groupedJobs = hashMapOf<String, WeakJob>()

    //<editor-fold desc="Add job ">
    private fun addJobToLocal(job: Job) {
        changeLocalJob { localJobs.add(job.weakReference()) }
        handleCompletedCompletion(job)
    }

    private fun addJobToGroup(job: Job, group: String) {
        changeGroupJob {
            this[group]?.get()?.cancel()
            this[group] = job.weakReference()
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
        changeLocalJob { removeAll { it.get()?.isCompleted != false } }
        changeGroupJob {
            this.filter { it.value.get() == null }
                    .forEach { remove(it.key) }
        }
    }


    fun cleanJobs() {
        localJobs.forEach { it.get()?.cancel() }
        groupedJobs.forEach { it.value.get()?.cancel() }
        queuedGroupedJobs.clear()
    }
    //</editor-fold>


    //<editor-fold desc="regular non grouped Actions">
    fun addJob(job: Job) = addJobToLocal(job)

    fun performAction(context: CoroutineContext, action: AsyncEmptyFunction): Job {
        val job = launch(context) { action() }
        addJobToLocal(job)
        return job
    }

    fun performAction(context: CoroutineContext, scopedAction: AsyncCoroutineFunction): Job {
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

    fun addJob(job: Job, group: String) = addJobToGroup(job, group)

    fun performAction(context: CoroutineContext,
                      scopedAction: AsyncCoroutineFunction,
                      forGroup: String): Job {
        val job = launch(context, block = scopedAction)
        addJobToGroup(job, forGroup)
        return job
    }

    fun performAction(context: CoroutineContext, action: AsyncEmptyFunction, forGroup: String): Job {
        val job = launch(context) { action() }
        addJobToGroup(job, forGroup)
        return job
    }
    //</editor-fold>

    //<editor-fold desc="Inline mutex functions">
    private inline fun changeLocalJob(crossinline action: MutableList<WeakJob>.() -> Unit) = runBlocking {
        localJobMutex.withLock {
            action(localJobs)
        }
    }

    private inline fun changeGroupJob(crossinline action: MutableMap<String, WeakJob>.() -> Unit) = runBlocking {
        groupJobMutex.withLock {
            action(groupedJobs)
        }
    }


    private inline fun changeQueuedJob(crossinline action: MutableMap<String, MutableList<WeakQueuedJob>>.() -> Unit) = runBlocking {
        queuedGroupedJobsMutex.withLock {
            action(queuedGroupedJobs)
        }
    }

    //</editor-fold>
    /**
     * Executes all queued up actions in that group.
     */
    fun executeQueue(group: String) = changeQueuedJob {
        get(group)?.let {
            it.forEach {
                it.use { performAction(first, second) }
            }
        }
        remove(group)
    }

    /**
     * Adds a given operation to a named queue.
     */
    fun addToQueue(context: CoroutineContext, action: AsyncEmptyFunction, group: String) = changeQueuedJob {
        getOrPut(group, { mutableListOf(Pair(context, action).weakReference()) })
    }

}