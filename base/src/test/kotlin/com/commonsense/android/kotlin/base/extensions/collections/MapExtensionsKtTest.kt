package com.commonsense.android.kotlin.base.extensions.collections

import android.util.SparseArray
import com.commonsense.android.kotlin.test.*
import org.junit.Test


/**
 * Created by Kasper Tvede on 08-10-2017.
 */
class MapExtensionsKtTest : BaseRoboElectricTest() {
    @Test
    fun forEachIndexed() {
        //test empty
        mapOf<String, String>().forEachIndexed { _, _ ->
            failTest("should not call with empty map")
        }

        //test num of callbacks
        testCallbackWithSemaphore(startAcquire = 2, startPermits = 0, shouldAcquire = true, errorMessage = "should be called 2 times") { sem ->
            mapOf(Pair("a", "1"), Pair("b", "2")).forEachIndexed { _, _ ->
                sem.release()
            }
        }

        var counter = 0
        mapOf(Pair("a", "1"), Pair("b", "2")).forEachIndexed { _, index ->
            counter.assert(index, "index should be incremental")
            counter += 1
        }


    }

    @Test
    fun map() {
        val sparseArray = SparseArray<String>()
        sparseArray.map { it }.assertSize(0, "empty should map nothing")

        for (i in 0 until 11) {
            sparseArray.put(i, "i:$i")
        }
        sparseArray.map { it }.assertSize(11, "creating 11 should map 11")
        sparseArray.map { it.length }.assertSize(11, "mapping to different type should still yield same count")
        sparseArray.map { it.length }.first().assert(3, "i:0 is 3 chars")
        sparseArray.map { it.length }.last().assert(4, "i:10 is 4 chars")
    }

    @Test
    fun forEach() {
        val sparseArray = SparseArray<String>()
        sparseArray.forEach { _ ->
            failTest("should not call with empty map")
        }

        sparseArray.put(1, "a")
        sparseArray.put(2, "b")


        //test num of callbacks
        testCallbackWithSemaphore(startAcquire = 2, startPermits = 0, shouldAcquire = true, errorMessage = "should be called 2 times") { sem ->
            sparseArray.forEach { _ ->
                sem.release()
            }
        }

    }

}