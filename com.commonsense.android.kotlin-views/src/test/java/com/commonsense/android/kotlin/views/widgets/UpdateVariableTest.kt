package com.commonsense.android.kotlin.views.widgets

import com.commonsense.android.kotlin.views.datastructures.UpdateVariable
import org.junit.Assert
import org.junit.Test

/**
 * Created by Kasper Tvede on 15-06-2017.
 */
class UpdateVariableTest {

    @Test
    fun testValue() {
        val toTest = UpdateVariable("testValue", {})
        Assert.assertEquals(toTest.value, "testValue")

        toTest.value = "newValue"
        Assert.assertEquals(toTest.value, "newValue")

        toTest.setWithNoUpdate("a-hidden-value")
        Assert.assertEquals(toTest.value, "a-hidden-value")
    }

    @Test
    fun testNotification() {
        var updatedVal = 0
        val toTest = UpdateVariable("testValue", { updatedVal += 1 })
        Assert.assertEquals(toTest.value, "testValue")
        toTest.value = "qwe"
        Assert.assertEquals(updatedVal, 1)//first update
        toTest.value = "qwe"
        Assert.assertEquals(updatedVal, 1)//nothing changed, so no update
        toTest.value = "qwe2"
        Assert.assertEquals(updatedVal, 2)//changed, so update

        toTest.setWithNoUpdate("qwe3")
        Assert.assertEquals(updatedVal, 2)//we told do not call update, so it should not happen


    }


}