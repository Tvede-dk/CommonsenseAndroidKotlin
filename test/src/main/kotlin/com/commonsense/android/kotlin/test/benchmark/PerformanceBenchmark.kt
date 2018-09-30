@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.test.benchmark

import com.commonsense.android.kotlin.test.*
import java.util.concurrent.*
import kotlin.system.*


/**
 * Created by Kasper Tvede on 14-04-2018.
 * Purpose:
 *
 */


inline fun microBench(
        numberOfIterations: Int = 200,
        numberOfRunsInIteration: Int = 100,
        warmCount: Int = 10,
        forceGcBetweenRuns: Boolean = false,
        limitMsPrInvocation: Int = 100,
        totalTimeoutInSeconds: Long = 10,
        crossinline action: () -> Unit) {
    val results = mutableListOf<Long>()
    val startTime = System.nanoTime()
    //warm-up phase
    for (warmI in 0 until warmCount) {
        action()

        if (TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - startTime) > totalTimeoutInSeconds) {
            failTest("Took too long for test. in warm up phase; iteration : $warmI of $warmCount")
            return
        }
    }
    forceGc()

    for (iteration in 0 until numberOfIterations) {
        if (forceGcBetweenRuns) {
            forceGc()
        }
        val time = measureNanoTime {
            for (run in 0 until numberOfRunsInIteration) {
                action()
            }
        }
        val timeInMs = TimeUnit.NANOSECONDS.toMillis(time)
        if (timeInMs > limitMsPrInvocation) {
            println("**** time was above threshold; $timeInMs ms," +
                    " ${timeInMs - limitMsPrInvocation} ms over the limit")
        }
        results.add(timeInMs)


        if (TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - startTime) > totalTimeoutInSeconds) {
            failTest("Took too long for test. iteration : $iteration of $numberOfIterations")
            return
        }
    }

    val totalTime = results.sum()
    results.sort()
    val average: Double = totalTime.toDouble() / (numberOfRunsInIteration * numberOfIterations).toDouble()
    val median = results[results.size / 2]
    println("--Result -- ")
    println("\t average pr iteration:${average}ms / median (over $numberOfRunsInIteration iterations) ${median}ms")
    println("\t largest time (over $numberOfRunsInIteration iterations) was: ${results.last()} ms")
    println("\t lowest time (over $numberOfRunsInIteration iterations) was: ${results.first()} ms")
    println("\t SD (over $numberOfRunsInIteration iterations) is: ${results.calculateSD()}")
    println("\t total test time (including overhead) was: ${TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - startTime)} seconds")
    println("\t total iterations: ${(numberOfRunsInIteration * numberOfIterations) + warmCount} over the total time")
    if (average > limitMsPrInvocation) {
        failTest("Test was to slow in average. average was $average ms but the limit was $limitMsPrInvocation ms")
    }
    println("--End -- ")
}


const val FORCE_GC_TIMEOUT_SECS: Long = 2

//From caliper
// simply cleans up, throws some garbage in, then when the garbage is collected, we can proceed.

fun forceGc() {
    System.gc()
    System.runFinalization()
    val latch = CountDownLatch(1)
    //create object that should be GC'ed.
    object : Any() {
        protected fun finalize() {
            latch.countDown()
        }
    }
    System.gc()
    System.runFinalization()
    try {
        latch.await(FORCE_GC_TIMEOUT_SECS, TimeUnit.SECONDS)
    } catch (e: InterruptedException) {
        Thread.currentThread().interrupt()
    }

}