@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.base.scheduling

import com.commonsense.android.kotlin.base.*
import com.commonsense.android.kotlin.base.debug.*
import com.commonsense.android.kotlin.base.extensions.*
import com.commonsense.android.kotlin.base.extensions.collections.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.*
import java.util.concurrent.*
import kotlin.coroutines.*


typealias QueuedJob = Pair<CoroutineContext, AsyncEmptyFunction>

/**
 * Created by Kasper Tvede on 22-06-2017.
 * It is meant for handling 3 types of scheduling
 */
open class JobContainer(val scope: CoroutineScope) {

    private val localJobs = ConcurrentLinkedQueue<Job>()

    private val queuedGroupedJobs = ConcurrentHashMap<String, MutableList<QueuedJob>>()

    private val groupedJobs = ConcurrentHashMap<String, Job>()

    //<editor-fold desc="Add job ">
    private fun addJobToLocal(job: Job) {
        localJobs.add(job)
        handleCompletedCompletion(job)
    }

    private fun addJobToGroup(job: Job, group: String) {
        groupedJobs[group]?.cancel()
        groupedJobs[group] = job
        handleCompletedCompletion(job)
    }
    //</editor-fold>

    private fun handleCompletedCompletion(job: Job) {
        job.invokeOnCompletion {
            removeDoneJobs()
        }
    }

    //<editor-fold desc="Description">
    fun removeDoneJobs() {
        localJobs.removeAll { it.isCompleted }
        groupedJobs.removeAll { it.value.isCompleted }
    }


    fun cleanJobs() {
        localJobs.forEach { it.cancel() }
        localJobs.clear()
        groupedJobs.forEach { it.value.cancel() }
        groupedJobs.clear()
        queuedGroupedJobs.clear()
    }
    //</editor-fold>


    //<editor-fold desc="regular non grouped Actions">
    fun addJob(job: Job) = addJobToLocal(job)

    fun performAction(context: CoroutineContext, action: AsyncEmptyFunction): Job {
        val scopedAction: AsyncCoroutineFunction = { action() }
        return performAction(context, scopedAction)
    }

    fun performAction(context: CoroutineContext, scopedAction: AsyncCoroutineFunction): Job {
        val job = GlobalScope.launch(context, block = scopedAction)
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
        val job = GlobalScope.launch(context, block = scopedAction)
        addJobToGroup(job, forGroup)
        return job
    }

    fun performAction(context: CoroutineContext,
                      action: AsyncEmptyFunction,
                      forGroup: String): Job {
        val scopedAction: AsyncCoroutineFunction = { action() }
        return performAction(context, scopedAction, forGroup)
    }
    //</editor-fold>

    //<editor-fold desc="Inline mutex functions">
//    private inline fun changeLocalJob(crossinline action: List<Job>.() -> List<Job>): Unit = runBlocking {
//        localJobMutex.withLock {
//            val result = action(localJobs)
//            localJobs.set(result)
//        }
//    }
//
//    private inline fun changeGroupJob(crossinline action: Map<String, Job>.() -> Map<String, Job>): Unit = runBlocking {
//        groupJobMutex.withLock {
//            val result = action(groupedJobs)
//            groupedJobs.clear()
//            groupedJobs.putAll(result)
//        }
//    }
//
//
//    private fun changeQueuedJob(
//            action: suspend Map<String, MutableList<QueuedJob>>.() -> Map<String, MutableList<QueuedJob>>)
//            : Unit = runBlocking {
//        queuedGroupedJobsMutex.withLock {
//            val result = action(queuedGroupedJobs)
//            queuedGroupedJobs.clear()
//            queuedGroupedJobs.putAll(result)
//        }
//    }

    //</editor-fold>

    /**
     * Executes all queued up actions in that group.
     * does not wait for the response of this.
     */
    fun executeQueueBackground(group: String) = scope.launch {
        executeQueueAwaited(group)
    }

    /**
     * Executes all queued up actions in that group.
     * waits until all jobs are "done":
     */
    suspend fun executeQueueAwaited(group: String) {
        //step 1 execute the group
        queuedGroupedJobs[group]?.map { performAction(it.first, it.second) }?.joinAll()
        //step 2 remove the group as it is not "queued" anymore
        queuedGroupedJobs.remove(group)
    }

    /**
     * Adds a given operation to a named queue.
     */
    fun addToQueue(context: CoroutineContext, action: AsyncEmptyFunction, group: String) {
//        return@changeQueuedJob this.toMutableMap().apply {
//            getOrPut(group) { mutableListOf() }.add(Pair(context, action))
//        }
        queuedGroupedJobs.getOrPut(group) { mutableListOf() }.add(Pair(context, action))
    }

    fun toPrettyString(): String {
        return "Job container state: " +
//                "\n\t\tLocal job mutex locked state: ${localJobMutex.isLocked}" +
//                "\n\t\tGroup job mutex lock state: ${groupJobMutex.isLocked}" +
//                "\n\t\tQueue job mutex lock state: ${queuedGroupedJobsMutex.isLocked}\n" +
                localJobs.map { "$it" }.prettyStringContent(
                        "\t\tlocal Jobs",
                        "\t\tno local jobs") +
                queuedGroupedJobs.map { "$it" }.prettyStringContent(
                        "\t\tQueue grouped jobs:",
                        "\t\tno queue grouped jobs") +
                groupedJobs.map { "$it" }.prettyStringContent(
                        "\t\tGrouped jobs",
                        "\t\tno grouped jobs")


    }

    override fun toString() = toPrettyString()

}