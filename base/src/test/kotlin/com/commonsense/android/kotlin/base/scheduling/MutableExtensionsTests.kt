package com.commonsense.android.kotlin.base.scheduling

import com.commonsense.android.kotlin.base.extensions.collections.*
import org.junit.*
import org.junit.jupiter.api.Test

/**
 * Created by Kasper Tvede on 09-07-2017.
 */
class MutableExtensionsTests {


    @Test
    fun testReplace() {
        val list = mutableListOf("1", "2")
        list.replace("3", 1)
        Assert.assertEquals(list[1], "3")
    }

}