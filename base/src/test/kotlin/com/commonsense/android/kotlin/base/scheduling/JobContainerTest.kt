@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.base.scheduling

import com.commonsense.android.kotlin.base.*
import com.commonsense.android.kotlin.base.extensions.*
import com.commonsense.android.kotlin.test.*
import kotlinx.coroutines.*
import org.junit.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Test
import java.lang.Exception
import java.util.concurrent.*
import java.util.concurrent.atomic.*

/**
 * Created by Kasper Tvede on 20-07-2017.
 */


class JobContainerTest {

    @Test
    fun testMassiveInsertRemove() = runBlocking {

        val jobContainer = JobContainer(this)
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
        val container = JobContainer(this)
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
        val container = JobContainer(this)
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
        val container = JobContainer(this)
        val slowSem = Semaphore(0)
        val slow2Sem = Semaphore(0)
        val slowJob = GlobalScope.launch {
            slowSem.acquire()
        }
        val slowJob2 = GlobalScope.launch {
            slow2Sem.acquire()
        }
        container.addJob(slowJob, "test")
        container.addJob(slowJob2, "test")


        container.getRemainingGroupedJobs().assert(1,
                "should have 2 job that is not done yet")
        slowSem.release()
        slowJob.join()
        container.getRemainingGroupedJobs().assert(1, "only(last) job should exists so no done yet.")
        delay(50)//since the onCompleted should be invoked.
        slow2Sem.release()
        slowJob2.join()
        delay(50)//since the onCompleted should be invoked.
        container.getRemainingGroupedJobs().assert(0, "should run last job")
    }

    @Test
    fun testQueuingAwaited() = runBlocking {
        val container = JobContainer(this)
        val counter = AtomicInteger(0)
        container.addToQueue(Dispatchers.Default, { counter.incrementAndGet() }, "test")
        container.addToQueue(Dispatchers.Default, { counter.incrementAndGet() }, "test")
        container.addToQueue(Dispatchers.Default, { counter.incrementAndGet() }, "test")
        counter.get().assert(0, " no jobs should run before ready.")

        container.executeQueueAwaited("test")
        counter.get().assert(3, "all should have randed")
    }

    @Test
    fun testQueueingBackground() = runBlocking {
        val container = JobContainer(this)
        val sem = Semaphore(1)
        container.addToQueue(Dispatchers.Default, { sem.release() }, "test")
        container.executeQueueBackground("test")
        sem.tryAcquire(1, 1000, TimeUnit.MILLISECONDS)
        Unit
    }

    @Test
    fun performAction() = runBlocking {
        val container = JobContainer(this)
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


        listOf(scopedJob, simpleJob).joinAll()
        container.getRemainingJobs().assert(0, "Should have finished all jobs")
        simpleCounter.assert(0)
        scopedCounter.assert(0)

    }

    /**
     * This tests that inserting another job for a group will discard the old job,
     * and only run the new one.
     *
     * for example this could be used in a button, where if the user preses it multiple times,
     * then you want to cancel the old and run the new.
     *
     */
    @Test
    fun performActionGrouped(): Unit = runBlocking {
        val container = JobContainer(this)

        val releasingSem = Semaphore(0, false)
        val presetupSem = Semaphore(0, false)

        //the counter that should never be 0, as the job should forcibly be canceled
        var simpleCounter = 1
        val simple: AsyncEmptyFunction = {
            presetupSem.release()
            idleForSemaphore(releasingSem)
            simpleCounter -= 1
        }

        var scopedCounter = 1
        val scoped: AsyncCoroutineFunction = {
            presetupSem.release()
            idleForSemaphore(releasingSem)
            scopedCounter -= 1
        }

        val simpleJob = container.performAction(Dispatchers.Default, simple, "A")
        presetupSem.tryAcquire(1, 500, TimeUnit.MILLISECONDS).assertTrue("should get the first job to start")

        val scopedJob = container.performAction(Dispatchers.Default, scoped, "A")
        presetupSem.tryAcquire(1, 500, TimeUnit.MILLISECONDS).assertTrue("the next job should also start")
        //assert active jobs
        container.getRemainingGroupedJobs()
                .assert(1, "there should only be one job for a group. the last one always wins.")
        container.getRemainingJobs().assert(0, "should have 0 non grouped jobs")
        //now let them finish
        simpleJob.isCancelled.assertTrue("should be canceled")
        delay(100)
        releasingSem.release(1)
//        println("is scopedJob canceled: ${scopedJob.isCancelled}")
        try {
            withTimeout(500) {

                println("simpleJob : ${simpleJob.isCancelled}")

                listOf(scopedJob, simpleJob).joinAll()
            }
        } catch (e: Exception) {
            println(e)
            println("simpleJob : $simpleJob")
            println("scopedJob : $scopedJob")
        }
//        println("got here")
        //assert that only the allowed job finished.
        container.getRemainingGroupedJobs().assert(0, "Should have finished all jobs")
        simpleCounter.assert(1, "should not have done its work" +
                " since it got canceled in favor of the second instead")
        scopedCounter.assert(0, "should have canceled the previous running group item.")
    }


    @Ignore
    @Test
    fun removeDoneJobs() {
    }

    @Test
    fun cleanJobs() = runBlocking {
        val container = JobContainer(this)
        container.addToQueue(Dispatchers.Default, { fail("should never be called") }, "test")
        container.addJob(GlobalScope.launch(start = CoroutineStart.LAZY) { fail("should never be called") }, "test")
        container.addJob(GlobalScope.launch(start = CoroutineStart.LAZY) { fail("should never be called") })
        container.cleanJobs()
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

    private suspend fun CoroutineScope.idleForSemaphore(semaphore: Semaphore, delayTimeInMs: Long = 20) {
        while (this.isActive) {
            if (semaphore.tryAcquire(1)) {
                break
            }
            delay(delayTimeInMs)
        }
    }
}