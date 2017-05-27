package com.commonsense.android.kotlin.extensions.collections

import android.util.SparseIntArray
import com.commonsense.android.kotlin.BaseRoboElectricTest
import org.junit.Assert
import org.junit.Test

/**
 * Created by Kasper Tvede on 27-05-2017.
 */
class CollectionExtensionsKtTest : BaseRoboElectricTest() {
    @Test
    fun clearAndSet() {
        val array = SparseIntArray()
        array.put(20, 20)
        Assert.assertEquals(array.size(), 1)
        array.clearAndSet(listOf(Pair(10, 10), Pair(30, 30)))
        Assert.assertEquals(array.size(), 2)
        Assert.assertEquals(array.get(10), 10)
        Assert.assertEquals(array.get(30), 30)
    }

    @Test
    fun clearAndSet1() {
        val array = SparseIntArray()
        array.put(20, 20)
        Assert.assertEquals(array.size(), 1)
        val map = HashMap<Int, Int>()
        map.put(10, 10)
        map.put(30, 30)
        array.clearAndSet(map)
        Assert.assertEquals(array.size(), 2)
        Assert.assertEquals(array[10], 10)
        Assert.assertEquals(array[30], 30)
    }

    @Test
    fun removeAtOr() {
        val list = mutableListOf("ab", "ba")
        Assert.assertEquals(list.removeAtOr(0, ""), "ab")
        Assert.assertEquals(list.size, 1)
        Assert.assertEquals(list.removeAtOr(10, "qwe"), "qwe")
        Assert.assertEquals(list.size, 1)
    }

    @Test
    fun isIndexValid() {
        val listOf2 = listOf("", "")
        Assert.assertFalse(listOf2.isIndexValid(20))

        Assert.assertFalse(listOf2.isIndexValid(-1))
        Assert.assertTrue(listOf2.isIndexValid(0))
        Assert.assertTrue(listOf2.isIndexValid(1))
        Assert.assertFalse(listOf2.isIndexValid(2))

        val listOf0 = listOf<String>()
        Assert.assertFalse(listOf0.isIndexValid(0))
        Assert.assertFalse(listOf0.isIndexValid(-1))
        Assert.assertFalse(listOf0.isIndexValid(1))


    }

    @Test
    fun findAndRemove() {
        val list = mutableListOf("a", "a", "b", "abc")
        list.findAndRemove { it == "a" }
        Assert.assertEquals(list.size, 3)
        list.findAndRemove { it == "b" }
        Assert.assertEquals(list.size, 2)
        list.findAndRemove { it == "b" }
        Assert.assertEquals(list.size, 2)
    }

    @Test
    fun findAndRemoveAll() {
        val list = mutableListOf("a", "a", "b", "abc")
        list.findAndRemoveAll { it == "a" }
        Assert.assertEquals(list.size, 2)
        list.findAndRemoveAll { it == "abc" }
        Assert.assertEquals(list.size, 1)
        list.findAndRemoveAll { it == "x" }
        Assert.assertEquals(list.size, 1)

    }

}
