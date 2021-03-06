package com.commonsense.android.kotlin.views.widgets

import com.commonsense.android.kotlin.test.*
import com.commonsense.android.kotlin.views.baseClasses.*
import org.junit.*
import org.robolectric.annotation.*

/**
 * Created by Kasper Tvede on 27-05-2017.
 */

@Config(manifest = Config.NONE)
class BaseAdapterTest : BaseRoboElectricTest() {

    @Test
    fun testIsIndexValidAndCount() {
        val adapter = BaseAdapter<String>(context)
        Assert.assertEquals(adapter.count, 0)
        Assert.assertFalse(adapter.isIndexValid(0))
        Assert.assertFalse(adapter.isIndexValid(-1))
        Assert.assertFalse(adapter.isIndexValid(1))

        adapter.add("1")
        Assert.assertTrue(adapter.isIndexValid(0))
        Assert.assertFalse(adapter.isIndexValid(1))
        Assert.assertEquals(adapter.count, 1)


        adapter.add("2")
        Assert.assertTrue(adapter.isIndexValid(0))
        Assert.assertTrue(adapter.isIndexValid(1))
        Assert.assertFalse(adapter.isIndexValid(2))
        Assert.assertEquals(adapter.count, 2)


        adapter.remove("1")
        Assert.assertTrue(adapter.isIndexValid(0))
        Assert.assertFalse(adapter.isIndexValid(1))
        Assert.assertEquals(adapter.count, 1)


        adapter.clear()
        Assert.assertFalse(adapter.isIndexValid(0))
        Assert.assertFalse(adapter.isIndexValid(-1))
        Assert.assertFalse(adapter.isIndexValid(1))
        Assert.assertEquals(adapter.count, 0)

    }


    @Test
    fun testFluentFunctions() {
        val adapter = BaseAdapter<String>(context)
        adapter.addAll("2", "1")
        Assert.assertEquals(adapter.getItem(0), "2")
        Assert.assertEquals(adapter.getItem(1), "1")


        adapter.addAll("3", "4")
        Assert.assertEquals(adapter.getItem(2), "3")
        Assert.assertEquals(adapter.getItem(3), "4")

        adapter.addAll(listOf("5", "7", "6"))
        Assert.assertEquals(adapter.getItem(4), "5")
        Assert.assertEquals(adapter.getItem(5), "7")
        Assert.assertEquals(adapter.getItem(6), "6")

    }

}