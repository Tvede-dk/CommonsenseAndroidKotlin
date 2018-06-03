package com.commonsense.android.kotlin.base.extensions.collections

import android.util.SparseArray
import android.util.SparseIntArray
import com.commonsense.android.kotlin.test.BaseRoboElectricTest
import com.commonsense.android.kotlin.test.assert
import com.commonsense.android.kotlin.test.assertNotNullAndEquals
import org.junit.Assert
import org.junit.Test


/**
 * Created by Kasper Tvede on 21-05-2018.
 * Purpose:
 */
class SpareArrayExtensionsKtTest : BaseRoboElectricTest() {

    @Test
    fun setViaList() {

        val arr = SparseIntArray()
        arr.append(50, 50)
        arr.set(listOf(50 to 10))
        arr.size().assert(1)
        arr.get(50).assert(10)
        arr.get(1).assert(0)
    }

    @Test
    fun setViaMap() {

        val arr = SparseIntArray()
        arr.append(50, 50)

        arr.set(mapOf(20 to 40))
        arr.size().assert(1)
        arr.get(20).assertNotNullAndEquals(40)
        arr.get(50).assert(0, "should give 06")

    }

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


}