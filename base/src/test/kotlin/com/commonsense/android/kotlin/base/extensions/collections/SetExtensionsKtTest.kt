package com.commonsense.android.kotlin.base.extensions.collections

import com.commonsense.android.kotlin.test.*
import org.junit.jupiter.api.*

/**
 * Created by Kasper Tvede on 21-05-2018.
 * Purpose:
 */
internal class SetExtensionsKtTest {

    @Test
    fun toggleExistence() {

        val set = mutableSetOf<Int>()
        set.assertEmpty()
        set.toggleExistence(42)
        set.assertSize(1)

        set.first().assert(42)

        set.toggleExistence(42)
        set.assertEmpty()


    }

    @Test
    fun setExistence() {

        val set = mutableSetOf<Int>()
        set.assertEmpty()
        set.setExistence(42, true)
        set.assertSize(1)
        set.setExistence(1, false)

        set.first().assert(42)

        set.assertSize(1)
        set.setExistence(42, false)
        set.assertEmpty()

    }
}