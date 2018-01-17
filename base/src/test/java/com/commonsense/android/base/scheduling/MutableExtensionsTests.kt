package com.commonsense.android.base.scheduling

import com.commonsense.android.kotlin.base.extensions.collections.replace
import org.junit.Assert
import org.junit.Test

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