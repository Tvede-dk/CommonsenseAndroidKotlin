package com.commonsense.android.kotlin.base.extensions.collections

import com.commonsense.android.kotlin.test.assert
import com.commonsense.android.kotlin.test.assertEmpty
import com.commonsense.android.kotlin.test.assertSize
import org.junit.jupiter.api.Test

/**
 * Created by Kasper Tvede on 21-05-2018.
 * Purpose:
 */
class MutableExtensionsKtTest {

    @Test
    fun findAndRemove() {
        val list = mutableListOf<String>("a", "b")
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

    }

    @Test
    fun replace() {

    }

    @Test
    fun set() {
        val list = mutableListOf<Int>(
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
    fun removeAll() {

    }

    @Test
    fun removeAtOr() {

    }
}