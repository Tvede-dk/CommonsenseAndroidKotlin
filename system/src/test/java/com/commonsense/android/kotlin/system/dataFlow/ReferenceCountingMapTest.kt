package com.commonsense.android.kotlin.system.dataFlow

import com.commonsense.android.kotlin.system.base.BaseActivity
import com.commonsense.android.kotlin.test.assert
import com.commonsense.android.kotlin.test.assertNull
import com.commonsense.android.kotlin.test.assertThrows
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Test

/**
 * Created by Kasper Tvede on 23-07-2017.
 */

class ReferenceCountingMapTest {

    private val index = "index"

    private val testValue = "testMe"

    //for the concurrency tests.
    private val maxSize = 10000
    private val maxJobs = 4

    @Test
    fun testReference() {
        val map = ReferenceCountingMap()
        map.hasItem(index).assert(false, "should not have item before insert.")
        map.addItem(testValue, index)
        map.hasItem(index).assert(true, "should have item after insert.")
        map.getItemAs<String>(index)?.assert(testValue)
        map.decrementCounter(index)
        map.hasItem(index).assert(false, "should remove item when no references are to it.")
    }

    @Test
    fun testCounting() {
        val map = ReferenceCountingMap()
        map.addItem(testValue, index)
        map.decrementCounter("missingIndex2")
        map.hasItem(index).assert(true, "decrement other index should have no effect on item")
        map.incrementCounter("missingIndex")
        map.decrementCounter(index)
        map.hasItem(index).assert(false, "increment other index should have no effect on item")
        map.decrementCounter(index)
        map.hasItem(index).assert(false, "decrement multiple times should have no impact.")
        map.addItem(testValue, index)
        assertThrows<Exception>("should throw error for trying to double add an item.") {
            map.addItem(testValue, index)
        }

        (0 until 20).forEach {
            map.incrementCounter(index)
        }
        map.hasItem(index).assert(true, "incrementing an item a lot should not affect its status(counter should be 21 now)")

        (0 until 19).forEach {
            map.decrementCounter(index)
            map.hasItem(index).assert(true, "should exists as the counter should be larger than 0")
        }
        map.decrementCounter(index)
        map.hasItem(index).assert(true, "counter should be 1")
        map.decrementCounter(index)
        map.hasItem(index).assert(false, "counter should have reached 0 by now.")

    }

    @Test
    fun testConverting() {
        val map = ReferenceCountingMap()
        map.addItem(testValue, index)
        map.getItemAs<String>(index)?.assert(testValue)
        map.getItemAs<Int>(index).assertNull()
        map.getItemAs<BaseActivity>(index).assertNull()
    }

    @Test
    fun testMultiThreadingAddRemove() = runBlocking {
        val map = ReferenceCountingMap()
        map.addItem(testValue, index)
        val incrementFunction = { _: Int ->
            map.incrementCounter(index)
        }
        val decrementFunction = { _: Int ->
            map.decrementCounter(index)
        }

        val incJobs = listOf(0 until maxJobs).map {
            async(CommonPool) {
                (0 until maxSize).forEach(incrementFunction)
            }
        }
        map.hasItem(index).assert(true)
        incJobs.forEach({ it.join() })
        map.hasItem(index).assert(true)

        val decJobs = listOf(0 until maxJobs).map {
            async(CommonPool) {
                (0 until maxSize).forEach(decrementFunction)
            }
        }
        map.hasItem(index).assert(true)
        decJobs.forEach({ it.join() })
        map.hasItem(index).assert(true)

        //counter should no matter what be 0 here.
        map.decrementCounter(index)
        map.hasItem(index).assert(false)
    }

    @Test
    fun testMultiThreadingMixed() = runBlocking {
        val map = ReferenceCountingMap()
        map.addItem(testValue, index)
        val incrementFunction = { _: Int ->
            map.incrementCounter(index)
        }
        val decrementFunction = { _: Int ->
            map.decrementCounter(index)
        }

        (0 until maxSize).forEach(incrementFunction)
        //at this point, we have maxSize+1 counts, now mix add and remove, (only to the half)

        val decJobs = listOf(0 until maxJobs / 2).map {
            async(CommonPool) {
                (0 until maxSize).forEach(decrementFunction)
            }
        }
        val incJobs = listOf(0 until maxJobs / 2).map {
            async(CommonPool) {
                (0 until maxSize).forEach(incrementFunction)
            }
        }
        map.hasItem(index).assert(true)
        decJobs.forEach({ it.join() })
        incJobs.forEach({ it.join() })
        map.hasItem(index).assert(true)

        //at this point if all is ok, we should have maxsize+1 refs.
        (0 until maxSize).forEach(decrementFunction)
        map.hasItem(index).assert(true)
        decrementFunction(0)
        map.hasItem(index).assert(false)

    }


}