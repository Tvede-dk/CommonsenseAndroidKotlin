package com.commonsense.android.kotlin.helperClasses

import kotlinx.coroutines.experimental.*
import org.junit.Assert
import org.junit.Test

/**
 * Created by Kasper Tvede on 22-06-2017.
 */


class JobContainerTest {

    @Test
    fun testMassiveInsertRemove() = runBlocking {

        val jobContainer = JobContainer()
        val jobs = mutableListOf<Job>()
        for (i in 0..(1000 - 1)) {
            jobs += launch(CommonPool) {
                jobContainer.performAction(CommonPool, action = { delay(1) })
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
        Assert.assertEquals(jobContainer.getRemainingJobs(), 0)

    }

}