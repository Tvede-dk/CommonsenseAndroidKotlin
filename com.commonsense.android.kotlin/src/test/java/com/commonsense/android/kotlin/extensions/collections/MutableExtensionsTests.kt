package com.commonsense.android.kotlin.extensions.collections

import com.commonsense.android.kotlin.BaseRoboElectricTest
import org.junit.Assert
import org.junit.Test

/**
 * Created by Kasper Tvede on 09-07-2017.
 */
class MutableExtensionsTests : BaseRoboElectricTest() {


    @Test
    fun testReplace() {
        val list = mutableListOf("1", "2")
        list.replace("3", 1)
        Assert.assertEquals(list[1], "3")
    }

}