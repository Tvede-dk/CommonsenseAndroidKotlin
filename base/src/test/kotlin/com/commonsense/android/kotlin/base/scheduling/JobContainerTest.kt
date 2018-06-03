package com.commonsense.android.kotlin.base.scheduling

import com.commonsense.android.kotlin.base.AsyncCoroutineFunction
import com.commonsense.android.kotlin.base.AsyncEmptyFunction
import com.commonsense.android.kotlin.base.extensions.awaitAll
import com.commonsense.android.kotlin.test.assert
import kotlinx.coroutines.experimental.*
import org.junit.jupiter.api.Test
import java.util.concurrent.Semaphore

/**
 * Created by Kasper Tvede on 20-07-2017.
 */


class JobContainerTest {

    @Test
    fun testMassiveInsertRemove() = runBlocking {

        val jobContainer = JobContainer()
        val jobs = mutableListOf<Job>()
        for (i in 0 until 1000) {
            jobs += launch(CommonPool) {
                jobContainer.performAction(CommonPool, action = { })
            }
            jobs += launch(CommonPool) {
                jobContainer.performAction(CommonPool, scopedAction = { })
            }
            jobs += launch(CommonPool) {
                jobContainer.removeDoneJobs()
            }
        }

        jobs.forEach { it.join() }
        jobContainer.performAction(CommonPool, action = { })
        jobContainer.removeDoneJobs()
        delay(100)
        jobContainer.getRemainingJobs().assert(0)

    }

    @Test
    fun testLaunchDelay() = runBlocking {
        val container = JobContainer()
        val slowJob = launch(CommonPool) {
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
        val slowJob = launch(CommonPool) {
            delay(300)
        }
        val slowJob2 = launch(CommonPool) {
            delay(500)
        }
        val slowJob3 = launch(CommonPool) {
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
        val slowJob = launch(CommonPool) {
            delay(200)
        }
        val slowJob2 = launch(CommonPool) {
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
        container.addToQueue(CommonPool, { counter += 1 }, "test")
        container.addToQueue(CommonPool, { counter += 1 }, "test")
        container.addToQueue(CommonPool, { counter += 1 }, "test")
        counter.assert(0, " no jobs should run before ready.")

        container.executeQueueAwaited("test")
        counter.assert(3, "all should have randed")
    }

    @Test
    fun testQueueingBackground() = runBlocking {
        val container = JobContainer()
        var counter = 1
        container.addToQueue(CommonPool, { counter -= 1 }, "test")
        container.executeQueueBackground("test")
        delay(50)
        counter.assert(0, "should have executed test even from the background")
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
        val simpleJob = container.performAction(CommonPool, simple)
        val scopedJob = container.performAction(CommonPool, scoped)
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
        val simpleJob = container.performAction(CommonPool, simple, "A")
        val scopedJob = container.performAction(CommonPool, scoped, "A")
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

}