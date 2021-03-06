@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.base.concurrency

import com.commonsense.android.kotlin.test.*
import kotlinx.coroutines.*
import org.junit.jupiter.api.*

/**
 * Created by Kasper Tvede on 20-05-2018.
 * Purpose:
 */
class LimitedCoroutineCounterTest {


    //TODO make this without delays... it only servers as an"example" of how the test / usage
    //should be
    @Suppress("DeferredResultUnused")
    @Test
    fun perform() = runBlocking {
        val limited = LimitedCoroutineCounter(2)
        var counter = 0
        GlobalScope.async {
            limited.perform(suspend {
                counter += 1
                delay(50)

            })
        }
        GlobalScope.async {
            limited.perform(suspend {
                counter += 1
                delay(50)
            })
        }
        GlobalScope.async {
            limited.perform(suspend {
                counter += 1
                delay(50)
            })
        }
        delay(40)
        counter.assert(2, "should only allow max count until tasks gets done")
        delay(80)
        counter.assert(3)

    }
}