package com.commonsense.android.kotlin.collections

import com.commonsense.android.kotlin.BaseRoboElectricTest
import com.commonsense.android.kotlin.extensions.collections.repeate
import org.junit.Assert
import org.junit.Test

/**
 * Created by kasper on 01/06/2017.
 */

class TestClassLookupHashcode<out T>(val someData: T, val viewType: Int) : TypeHashCodeLookup {
    override fun getTypeValue(): Int = viewType
}

class TypeLookupCollectionTest : BaseRoboElectricTest() {

    @Test
    fun testInsertDifferentItems() {
        val collection = TypeLookupCollection<TestClassLookupHashcode<*>>()
        val onlyClass = TestClassLookupHashcode("asd", 1)
        collection.add(onlyClass)
        Assert.assertEquals(collection.getCount(), 1)
        Assert.assertEquals(collection[0], onlyClass)
        Assert.assertEquals(collection.getAnItemFromType(1), onlyClass)

        val otherClass = TestClassLookupHashcode("qwe", 2)
        collection.add(otherClass)
        Assert.assertEquals(collection.getCount(), 2)
        Assert.assertEquals(collection[1], otherClass)
        Assert.assertEquals(collection.getAnItemFromType(2), otherClass)


    }

    @Test
    fun testGetAnItemFromType() {
        val collection = TypeLookupCollection<TestClassLookupHashcode<*>>()
        val firstClass = TestClassLookupHashcode("asd", 1)
        val secondClass = TestClassLookupHashcode("asd", 2)
        val thirdClass = TestClassLookupHashcode("asd", 3)
        val firstSecClass = TestClassLookupHashcode("asd2", 1)
        collection.addAll(firstClass, secondClass, thirdClass, firstSecClass)
        Assert.assertEquals(collection.getCount(), 4)
        Assert.assertEquals(collection.getAnItemFromType(1), firstClass)
        Assert.assertEquals(collection.getAnItemFromType(2), secondClass)
        Assert.assertEquals(collection.getAnItemFromType(3), thirdClass)
        //the firstSecClass should be there, its just not the first type (1) that gets inserted.
        Assert.assertEquals(collection.getItemAt(3), firstSecClass)
    }

    @Test
    fun testGetAnItemFromTypeRemoveal() {
        val collection = TypeLookupCollection<TestClassLookupHashcode<*>>()
        val firstClass = TestClassLookupHashcode("asd", 1)
        val secondClass = TestClassLookupHashcode("asd", 2)
        val thirdClass = TestClassLookupHashcode("asd", 3)
        val firstSecClass = TestClassLookupHashcode("asd2", 1)
        collection.addAll(firstClass, secondClass, thirdClass, firstSecClass)
        Assert.assertEquals(collection.getCount(), 4)
        Assert.assertEquals(collection.getAnItemFromType(1), firstClass)
        collection.removeAt(0)
        Assert.assertEquals(collection.getAnItemFromType(1), firstSecClass)
        collection.remove(firstSecClass)
        Assert.assertEquals(collection.getAnItemFromType(1), null)


    }


    @Test
    fun testRemove() {
        val collection = TypeLookupCollection<TestClassLookupHashcode<*>>()
        val firstClass = TestClassLookupHashcode("asd", 1)
        collection.addAll(listOf(firstClass).repeate(10))
        Assert.assertEquals(collection.getCount(), 10)
        collection.remove(collection[5]!!)
        collection.remove(collection[7]!!)
        collection.remove(collection[1]!!)
        Assert.assertEquals(collection.getCount(), 7)
    }

    @Test
    fun testRemoveAll() {
        val collection = TypeLookupCollection<TestClassLookupHashcode<*>>()
        val firstClass = TestClassLookupHashcode("asd", 1)
        val secondClass = TestClassLookupHashcode("asd", 2)
        val thirdClass = TestClassLookupHashcode("asd", 3)
        val firstSecClass = TestClassLookupHashcode("asd2", 1)
        collection.addAll(firstClass, secondClass, thirdClass, firstSecClass)
        Assert.assertEquals(collection.getCount(), 4)

        collection.removeAll(firstClass, firstSecClass, thirdClass)
        Assert.assertEquals(collection.getCount(), 1)
        Assert.assertEquals(collection.getAnItemFromType(1), null)
        Assert.assertEquals(collection.getAnItemFromType(2), secondClass)
        Assert.assertEquals(collection.getAnItemFromType(3), null)
    }


    @Test
    fun clear() {
        val collection = TypeLookupCollection<TestClassLookupHashcode<*>>()
        val firstClass = TestClassLookupHashcode("asd", 1)
        collection.addAll(listOf(firstClass).repeate(10))
        Assert.assertEquals(collection.getCount(), 10)
        collection.clear()
        Assert.assertEquals(collection.getCount(), 0)
        Assert.assertEquals(collection.getAnItemFromType(1), null)

    }

}