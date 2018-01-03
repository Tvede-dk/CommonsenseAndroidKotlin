package com.commonsense.android.kotlin.base.scheduling

import com.commonsense.android.kotlin.test.assert
import kotlinx.coroutines.experimental.*
import org.junit.Test

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

        delay(200)
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
    fun testQueuing() {
        val container = JobContainer()
        var counter = 0
        container.addToQueue(CommonPool, { counter++ }, "test")
        container.addToQueue(CommonPool, { counter++ }, "test")
        container.addToQueue(CommonPool, { counter++ }, "test")
        counter.assert(0, " no jobs should run before ready.")
        container.executeQueue("test")
        counter.assert(3, "all should have randed")
    }

}