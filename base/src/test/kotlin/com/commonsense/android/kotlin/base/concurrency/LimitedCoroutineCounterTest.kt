package com.commonsense.android.kotlin.base.concurrency

import com.commonsense.android.kotlin.test.assert
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import org.junit.jupiter.api.Test

/**
 * Created by Kasper Tvede on 20-05-2018.
 * Purpose:
 */
class LimitedCoroutineCounterTest {


    //TODO make this without delays... it only servers as an"example" of how the test / usage
    //should be
    @Test
    fun perform() = runBlocking {
        val limited = LimitedCoroutineCounter(2)
        var counter = 0
        async {
            limited.perform(suspend {
                counter += 1
                delay(50)

            })
        }
        async {
            limited.perform(suspend {
                counter += 1
                delay(50)
            })
        }
        async {
            limited.perform(suspend {
                counter += 1
                delay(50)
            })
        }
        delay(10)
        counter.assert(2, "should only allow max count until tasks gets done")
        delay(80)
        counter.assert(3)

    }
}