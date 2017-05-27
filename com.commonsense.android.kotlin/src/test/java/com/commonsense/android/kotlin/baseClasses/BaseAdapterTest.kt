package com.commonsense.android.kotlin.baseClasses

import com.commonsense.android.kotlin.BaseRoboElectricTest
import org.junit.Assert
import org.junit.Test

/**
 * Created by Kasper Tvede on 27-05-2017.
 */
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

    


}