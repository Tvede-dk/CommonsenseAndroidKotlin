package com.commonsense.android.kotlin.base.extensions.collections

import com.commonsense.android.kotlin.test.*
import org.junit.*
import org.junit.jupiter.api.Test

/**
 * Created by Kasper Tvede on 21-05-2018.
 * Purpose:
 */
class MutableExtensionsKtTest {

    @Test
    fun findAndRemove() {
        val list = mutableListOf("a", "b")
        list.findAndRemove { it == "a" }
        list.assertSize(1, "should have removed a")
        list.first().assert("b")
        list.addAll(listOf("a", "a", "b"))
        list.findAndRemove { it == "a" }
        list.assertSize(3, "should have 1 a and 2 b's ")
        list.first().assert("b")
        list.findAndRemove { it == "b" }
        list.assertSize(2, "should have 1 a and 1 b")
        list.first().assert("a")
        list.last().assert("b")
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

    @Ignore
    @Test
    fun replace() {

    }

    @Test
    fun set() {
        val list = mutableListOf(
                42, 2, 1
        )
        list.set(listOf())
        list.assertEmpty("should have removed all things before setting")
        list.set(listOf(989))
        list.assertSize(1)
        list.first().assert(989)
        list.set(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))
        list.assertSize(10)
        list.first().assert(1)
        list.last().assert(10)


    }

    @Test
    fun setSingle() {
        val list = mutableListOf<String>()
        list.add("test")
        list.add("test2")
        list.set("nope")
        list.assertSize(1,"setting a single item should give 1 item")
        list.first().assert("nope","should have set item")
        list.set("test23")
        list.assertSize(1)
        list.first().assert("test23")
    }

    @Ignore
    @Test
    fun removeAll() {

    }

    @Test
    fun removeAtOr() {
        val list = mutableListOf("ab", "ba")
        Assert.assertEquals(list.removeAtOr(0, ""), "ab")
        Assert.assertEquals(list.size, 1)
        Assert.assertEquals(list.removeAtOr(10, "qwe"), "qwe")
        Assert.assertEquals(list.size, 1)
    }

    @Ignore
    @Test
    fun set1() {
    }

}