package com.commonsense.android.kotlin.base.scheduling

import com.commonsense.android.kotlin.base.AsyncCoroutineFunction
import com.commonsense.android.kotlin.base.AsyncEmptyFunction
import com.commonsense.android.kotlin.base.extensions.collections.set
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import kotlinx.coroutines.experimental.sync.Mutex
import kotlinx.coroutines.experimental.sync.withLock
import kotlin.coroutines.experimental.CoroutineContext

        /**
         * Created by Kasper Tvede on 22-06-2017.
         */

typealias QueuedJob = Pair<CoroutineContext, AsyncEmptyFunction>

open class JobContainer {

    private val localJobMutex = Mutex()

    private val groupJobMutex = Mutex()

    private val queuedGroupedJobsMutex = Mutex()

    private val localJobs = mutableListOf<Job>()

    private val queuedGroupedJobs = hashMapOf<String, MutableList<QueuedJob>>()

    private val groupedJobs = hashMapOf<String, Job>()

    //<editor-fold desc="Add job ">
    private fun addJobToLocal(job: Job) {
        changeLocalJob { this.toMutableList().apply { add(job) } }
        handleCompletedCompletion(job)
    }

    private fun addJobToGroup(job: Job, group: String) {
        changeGroupJob {
            val res = toMutableMap()
            res[group]?.cancel()
            res[group] = job
            return@changeGroupJob res
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
        changeLocalJob { filterNot { it.isCompleted } }
        changeGroupJob { filterNot { it.value.isCompleted } }
    }


    fun cleanJobs() {
        localJobs.forEach { it.cancel() }
        groupedJobs.forEach { it.value.cancel() }
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
    private inline fun changeLocalJob(crossinline action: List<Job>.() -> List<Job>): Unit = runBlocking {
        localJobMutex.withLock {
            val result = action(localJobs)
            localJobs.set(result)
        }
    }

    private inline fun changeGroupJob(crossinline action: Map<String, Job>.() -> Map<String, Job>): Unit = runBlocking {
        groupJobMutex.withLock {
            val result = action(groupedJobs)
            groupedJobs.clear()
            groupedJobs.putAll(result)
        }
    }


    private inline fun changeQueuedJob(crossinline action: Map<String, MutableList<QueuedJob>>.() -> Map<String, MutableList<QueuedJob>>)
            : Unit = runBlocking {
        queuedGroupedJobsMutex.withLock {
            val result = action(queuedGroupedJobs)
            queuedGroupedJobs.clear()
            queuedGroupedJobs.putAll(result)
        }
    }

    //</editor-fold>
    /**
     * Executes all queued up actions in that group.
     */
    fun executeQueue(group: String): Unit = changeQueuedJob {
        get(group)?.let {
            it.forEach {
                performAction(it.first, it.second)
            }
        }
        return@changeQueuedJob this.filter { it.key != group }
    }

    /**
     * Adds a given operation to a named queue.
     */
    fun addToQueue(context: CoroutineContext, action: AsyncEmptyFunction, group: String): Unit = changeQueuedJob {
        return@changeQueuedJob this.toMutableMap().apply {
            getOrPut(group, { mutableListOf() }).add(Pair(context, action))
        }
    }

}