@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.base.scheduling

import com.commonsense.android.kotlin.base.*
import com.commonsense.android.kotlin.base.extensions.*
import com.commonsense.android.kotlin.test.*
import kotlinx.coroutines.*
import org.junit.*
import org.junit.jupiter.api.Test
import java.util.concurrent.*

/**
 * Created by Kasper Tvede on 20-07-2017.
 */


class JobContainerTest {

    @Test
    fun testMassiveInsertRemove() = runBlocking {

        val jobContainer = JobContainer()
        val jobs = mutableListOf<Job>()
        for (i in 0 until 1000) {
            jobs += GlobalScope.launch {
                jobContainer.performAction(Dispatchers.Default, action = { })
            }
            jobs += GlobalScope.launch {
                jobContainer.performAction(Dispatchers.Default, scopedAction = { })
            }
            jobs += GlobalScope.launch {
                jobContainer.removeDoneJobs()
            }
        }

        jobs.forEach { it.join() }
        jobContainer.performAction(Dispatchers.Default, action = { })
        jobContainer.removeDoneJobs()
        delay(100)
        jobContainer.getRemainingJobs().assert(0)

    }

    @Test
    fun testLaunchDelay() = runBlocking {
        val container = JobContainer()
        val slowJob = GlobalScope.launch {
            delay(200)
        }
        container.addJob(slowJob, "test")
        container.getRemainingGroupedJobs().assert(1,
                "should have 1 job that is not done yet")

        delay(300)
        container.getRemainingGroupedJobs().assert(0, "jobs should have finished.")
    }

    @Test
    fun testLaunchDelayAdvanced() = runBlocking {
        val container = JobContainer()
        val slowJob = GlobalScope.launch {
            delay(300)
        }
        val slowJob2 = GlobalScope.launch {
            delay(500)
        }
        val slowJob3 = GlobalScope.launch {
            delay(100)
        }
        container.addJob(slowJob, "test")
        container.addJob(slowJob2, "test2")
        container.addJob(slowJob3, "test3")
        container.getRemainingGroupedJobs().assert(3,
                "should have 2 job that is not done yet")
        delay(200)
        container.getRemainingGroupedJobs().assert(2, "job should have finished.")
        delay(150)
        container.getRemainingGroupedJobs().assert(1, "job should have finished.")
        delay(300)
        container.getRemainingGroupedJobs().assert(0, "job should have finished.")
    }

    @Test
    fun testDuplicatedGroups() = runBlocking {
        val container = JobContainer()
        val slowJob = GlobalScope.launch {
            delay(200)
        }
        val slowJob2 = GlobalScope.launch {
            delay(400)
        }
        container.addJob(slowJob, "test")
        container.addJob(slowJob2, "test")
        container.getRemainingGroupedJobs().assert(1,
                "should have 2 job that is not done yet")
        delay(250)
        container.getRemainingGroupedJobs().assert(1, "only(last) job should exists so no done yet.")
        delay(250)
        container.getRemainingGroupedJobs().assert(0, "only(last) job should exists so no done yet.")
    }

    @Test
    fun testQueuingAwaited() = runBlocking {
        val container = JobContainer()
        var counter = 0
        container.addToQueue(Dispatchers.Default, { counter += 1 }, "test")
        container.addToQueue(Dispatchers.Default, { counter += 1 }, "test")
        container.addToQueue(Dispatchers.Default, { counter += 1 }, "test")
        counter.assert(0, " no jobs should run before ready.")

        container.executeQueueAwaited("test")
        counter.assert(3, "all should have randed")
    }

    @Test
    fun testQueueingBackground() {
        val container = JobContainer()
        val sem = Semaphore(1)
        container.addToQueue(Dispatchers.Default, { sem.release() }, "test")
        container.executeQueueBackground("test")
        sem.tryAcquire(1, 1000, TimeUnit.MILLISECONDS)
    }

    @Test
    fun performAction() = runBlocking {
        val container = JobContainer()
        var simpleCounter = 1
        val sem = Semaphore(0, false)
        val simple: AsyncEmptyFunction = {
            sem.acquire()
            simpleCounter -= 1
        }

        var scopedCounter = 1
        val scoped: AsyncCoroutineFunction = {
            sem.acquire()
            scopedCounter -= 1
        }
        val simpleJob = container.performAction(Dispatchers.Default, simple)
        val scopedJob = container.performAction(Dispatchers.Default, scoped)
        container.getRemainingJobs()
                .assert(2, "jobs that are not done should not be counted as")
        sem.release(2)


        listOf(scopedJob, simpleJob).awaitAll()
        container.getRemainingJobs().assert(0, "Should have finished all jobs")
        simpleCounter.assert(0)
        scopedCounter.assert(0)

    }

    @Test
    fun performActionGrouped() = runBlocking {
        val container = JobContainer()
        var simpleCounter = 1

        val releasingSem = Semaphore(0, false)
        val presetupSem = Semaphore(0, false)

        val simple: AsyncEmptyFunction = {
            presetupSem.release()
            while (this.isActive) {
                if (releasingSem.tryAcquire(1)) {
                    break
                }
                delay(1)
            }
            simpleCounter -= 1
        }

        var scopedCounter = 1
        val scoped: AsyncCoroutineFunction = {
            presetupSem.release()
            while (this.isActive) {
                if (releasingSem.tryAcquire(1)) {
                    break
                }
                delay(1)
            }
            scopedCounter -= 1
        }
        val simpleJob = container.performAction(Dispatchers.Default, simple, "A")
        val scopedJob = container.performAction(Dispatchers.Default, scoped, "A")
        presetupSem.acquire(2)
        container.getRemainingGroupedJobs()
                .assert(1, "there should only be one job for a group. the last one always wins.")

        container.getRemainingJobs().assert(0, "should have 0 non grouped jobs")
        releasingSem.release(1)

        listOf(scopedJob, simpleJob).awaitAll()
        container.getRemainingGroupedJobs().assert(0, "Should have finished all jobs")
        simpleCounter.assert(1, "should not have done its work" +
                " since it got canceled in favor of the second instead")
        scopedCounter.assert(0, "should have canceled the previous running group item.")
    }

    @Ignore
    @Test
    fun removeDoneJobs() {
    }

    @Ignore
    @Test
    fun cleanJobs() {
    }

    @Ignore
    @Test
    fun addJob() {
    }

    @Ignore
    @Test
    fun performAction1() {
    }

    @Ignore
    @Test
    fun getRemainingJobs() {
    }

    @Ignore
    @Test
    fun getRemainingGroupedJobs() {
    }

    @Ignore
    @Test
    fun addJob1() {
    }

    @Ignore
    @Test
    fun performAction2() {
    }

    @Ignore
    @Test
    fun performAction3() {
    }

    @Ignore
    @Test
    fun executeQueueBackground() {
    }

    @Ignore
    @Test
    fun executeQueueAwaited() {
    }

    @Ignore
    @Test
    fun addToQueue() {
    }

    @Ignore
    @Test
    fun toPrettyString() {
    }
}