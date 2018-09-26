package com.commonsense.android.kotlin.base.extensions

import com.commonsense.android.kotlin.test.assert
import com.commonsense.android.kotlin.test.assertSize
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.channels.Channel
import org.junit.*
import org.junit.jupiter.api.Test
import java.util.concurrent.Semaphore

/**
 * Created by Kasper Tvede on 22-05-2018.
 * Purpose:
 */
class CoroutinesExtensionsKtTest {

    @Test
    fun launchOnCompleted() = runBlocking {

        val sem = Semaphore(0, false)
        var counter = 0
        val job = launch(CommonPool) {
            counter += 1
            delay(5)
            counter += 1
        }.apply {
            launchOnCompleted(CommonPool) {
                counter.assert(2, "should have completed job")
                counter += 1
                sem.release()
            }
        }
        job.join()
        sem.acquire()
        counter.assert(3, "should have done the onCompleted")

    }

    @Test
    fun launchOnCompletedAsync() = runBlocking {
        val sem = Semaphore(0, false)
        var counter = 0
        val job = launch(CommonPool) {
            counter += 1
            delay(5)
            counter += 1
        }.apply {
            launchOnCompletedAsync(CommonPool) {
                counter.assert(2, "should have completed job")
                delay(5)
                counter += 11
                delay(2)
                sem.release()
            }
        }
        job.join()
        sem.acquire()
        counter.assert(13, "should have done the onCompleted")
    }

    @Ignore
    @Test
    fun launchBlock() = runBlocking {
        var counter = 0
        val job = launchBlock(CommonPool) {
            counter += 1
        }

    }

    @Test
    fun asyncSimple() = runBlocking {
        val simpleLazy = asyncSimple(CommonPool, CoroutineStart.LAZY) {
            "someValue"
        }
        simpleLazy.start()
        val awaited = simpleLazy.await()
        awaited.assert("someValue")
    }

    @Test
    fun asyncSimple1() = runBlocking {
        val simple = asyncSimple(CommonPool) {
            42
        }
        simple.await().assert(42)
    }

    @Test
    fun await() = runBlocking {

        val deferred: List<Deferred<Int>> = listOf(
                async(CommonPool) { 41 },
                async(CommonPool) { 42 },
                async(CommonPool) { 9 },
                async(CommonPool) { 8 }
        )
        val results = deferred.await()
        results.assertSize(4)
        results.count { it == 8 }.assert(1)
        results.count { it == 9 }.assert(1)
        results.count { it == 42 }.assert(1)
        results.count { it == 41 }.assert(1)
        results.sum().assert(100)
    }

    @Test
    fun awaitAll() = runBlocking {

        var counter = 0
        val jobs = listOf(
                launch(CommonPool) {
                    delay(20)
                    counter += 1
                },
                launch(CommonPool) {
                    counter += 1
                    delay(20)
                    counter += 1
                }
        )
        jobs.awaitAll()
        counter.assert(3, "should have done all combinations of jobs before reaching here.")
    }

    @Ignore
    @Test
    fun forEachSync() = runBlocking {
//        var combinedResult = 0
//        val semaphore = Semaphore(0)
//        val channel = Channel<Int>()
//        async {
//            channel.forEach {
//                combinedResult += it
//                semaphore.release()
//            }
//        }
//
//        channel.send(1)
//        channel.send(2)
//        channel.send(3)
//        channel.send(4)
//        channel.close()
//        semaphore.acquire(4)
//        combinedResult.assert(4 + 3 + 2 + 1)
    }

    @Ignore
    @Test
    fun forEachAsync() = runBlocking {
//        var combinedResult = 0
//        val semaphore = Semaphore(0)
//        val channel = Channel<Int>()
//        async {
//            channel.forEachAsync {
//                combinedResult += it
//                semaphore.release()
//            }
//        }
//
//        channel.send(1)
//        channel.send(2)
//        channel.send(3)
//        channel.send(4)
//        channel.close()
//        semaphore.acquire(4)
//        combinedResult.assert(4 + 3 + 2 + 1)
    }

    @Ignore
    @Test
    fun forEach() {
    }
}

