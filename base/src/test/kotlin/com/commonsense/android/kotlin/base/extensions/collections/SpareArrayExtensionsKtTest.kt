@file:Suppress("UNUSED_ANONYMOUS_PARAMETER")

package com.commonsense.android.kotlin.base.extensions.collections

import android.util.*
import com.commonsense.android.kotlin.test.*
import org.junit.*


/**
 * Created by Kasper Tvede on 21-05-2018.
 * Purpose:
 */
class SpareArrayExtensionsKtTest : BaseRoboElectricTest() {


    @Test
    fun toList() {

        val arr = SparseArray<Int>()

        arr.toList().apply {
            size.assert(0)
        }

        arr.append(0, 1)
        arr.append(100, 2)

        arr.toList().apply {
            size.assert(2)
            first().apply {
                key.assert(0)
                value.assert(1)
            }
            last().apply {
                key.assert(100)
                value.assert(2)
            }
        }
        arr.append(50, 50)
        arr.toList(50).apply {
            size.assert(2)
            first().apply {
                key.assert(0)
                value.assert(1)
            }
            last().apply {
                key.assert(50)
                value.assert(50)
            }
        }
    }

    @Test
    fun findFirst() {

        val arr = SparseArray<Int>()
        arr.findFirst { key, item -> true }
                .assertNull("should not be able to find anything in empty array")


        arr.append(0, 20)
        arr.findFirst { key, item ->
            key == 0
        }.assertNotNullApply {
            this.key.assert(0)
            this.value.assert(20)
        }

        arr.append(1000, 20)
        arr.findFirst { key, item -> key == 1000 }.assertNotNullApply {
            key.assert(1000)
            value.assert(20)
        }

        arr.append(500, 200)

        arr.findFirst { key, item -> item == 200 }.assertNotNullApply {
            key.assert(500)
            value.assert(200)
        }

        arr.findFirst { key, item -> key == 1001 }.assertNull()

        arr.findFirst { key, item -> key == item }.assertNull("no key is the same as item")


    }

    @Test
    fun set() {
        setViaList()
    }

    @Test
    fun set1() {
        setViaMap()
    }

    @Test
    fun map() {
        val start = SparseArray<Int>()
        start.map { }.assertSize(0)
        start.put(42, 0)
        start.map { it }.assertSize(1)
        start.put(44, 1)
        start.map { it }.assertSize(2)


        start.map { it }.apply {
            first().assert(0)
            last().assert(1)
        }
        start.map { "$it" }.apply {
            first().assert("0")
            last().assert("1")
        }
    }

    @Test
    fun forEach() {

        val start = SparseArray<Int>()
        start.forEach { failTest("is empty") }
        start.put(0, 0)
        start.forEach { it.assert(0) }

        start.put(5, 5)
        start.put(2, 2)
        var counter = 0
        val seenValues = mutableListOf<Int>()
        start.forEach { counter += 1; seenValues.add(it) }
        counter.assert(3)
        seenValues.assertSize(3)
        seenValues.first().assert(0)
        seenValues[1].assert(2)
        seenValues[2].assert(5)
    }

    @Ignore
    @Test
    fun binarySearch() {

    }

    fun setViaList() {

        val arr = SparseIntArray()
        arr.append(50, 50)
        arr.set(listOf(50 to 10))
        arr.size().assert(1)
        arr.get(50).assert(10)
        arr.get(1).assert(0)
    }

    fun setViaMap() {

        val arr = SparseIntArray()
        arr.append(50, 50)

        arr.set(mapOf(20 to 40))
        arr.size().assert(1)
        arr.get(20).assertNotNullAndEquals(40)
        arr.get(50).assert(0, "should give 06")

    }
}