package com.commonsense.android.kotlin.collections

import com.commonsense.android.kotlin.BaseRoboElectricTest
import com.commonsense.android.kotlin.extensions.collections.repeate
import com.commonsense.android.kotlin.extensions.collections.repeateToSize
import org.junit.Assert
import org.junit.Test
import kotlin.system.measureNanoTime

/**
 * Created by Kasper Tvede on 05-06-2017.
 */

class TestClassTypeLookupHashcode<out T>(val someData: T, val viewType: Int) : TypeHashCodeLookupRepresent<String> {
    override fun getInflaterFunction(): String {
        return ""
    }

    override fun getTypeValue(): Int = viewType
}


class TypeLookupCollectionRepresentiveTest : BaseRoboElectricTest() {
    @Test
    fun testRemove() {
        val collection = TypeLookupCollectionRepresentive<TestClassTypeLookupHashcode<*>, String>()
        val firstClass = TestClassTypeLookupHashcode("asd", 1)
        collection.addAll(listOf(firstClass).repeate(10))
        Assert.assertEquals(collection.size, 10)
        collection.remove(collection[5]!!)
        collection.remove(collection[7]!!)
        collection.remove(collection[1]!!)
        Assert.assertEquals(collection.size, 7)
    }

    @Test
    fun testRemoveAll() {
        val collection = TypeLookupCollectionRepresentive<TestClassTypeLookupHashcode<*>, String>()
        val firstClass = TestClassTypeLookupHashcode("asd", 1)
        val secondClass = TestClassTypeLookupHashcode("asd", 2)
        val thirdClass = TestClassTypeLookupHashcode("asd", 3)
        val firstSecClass = TestClassTypeLookupHashcode("asd2", 1)
        collection.addAll(firstClass, secondClass, thirdClass, firstSecClass)
        Assert.assertEquals(collection.size, 4)

        collection.removeAll(firstClass, firstSecClass, thirdClass)
        Assert.assertEquals(collection.size, 1)
        Assert.assertEquals(collection.getTypeRepresentativeFromTypeValue(1), null)
        Assert.assertEquals(collection.getTypeRepresentativeFromTypeValue(2), secondClass.getInflaterFunction())
        Assert.assertEquals(collection.getTypeRepresentativeFromTypeValue(3), null)
    }


    @Test
    fun clear() {
        val collection = TypeLookupCollectionRepresentive<TestClassTypeLookupHashcode<*>, String>()
        val firstClass = TestClassTypeLookupHashcode("asd", 1)
        collection.addAll(listOf(firstClass).repeate(10))
        Assert.assertEquals(collection.size, 10)
        collection.clear()
        Assert.assertEquals(collection.size, 0)
        Assert.assertEquals(collection.getTypeRepresentativeFromTypeValue(1), null)

    }

    @Test
    fun testInsertDifferentItems() {
        val collection = TypeLookupCollectionRepresentive<TestClassTypeLookupHashcode<*>, String>()
        val onlyClass = TestClassTypeLookupHashcode("asd", 1)
        collection.add(onlyClass)
        Assert.assertEquals(collection.size, 1)
        Assert.assertEquals(collection[0], onlyClass)
        Assert.assertEquals(collection.getTypeRepresentativeFromTypeValue(1), onlyClass.getInflaterFunction())

        val otherClass = TestClassTypeLookupHashcode("qwe", 2)
        collection.add(otherClass)
        Assert.assertEquals(collection.size, 2)
        Assert.assertEquals(collection[1], otherClass)
        Assert.assertEquals(collection.getTypeRepresentativeFromTypeValue(2), otherClass.getInflaterFunction())


    }

    @Test
    fun testGetAnItemFromType() {
        val collection = TypeLookupCollectionRepresentive<TestClassTypeLookupHashcode<*>, String>()
        val firstClass = TestClassTypeLookupHashcode("asd", 1)
        val secondClass = TestClassTypeLookupHashcode("asd", 2)
        val thirdClass = TestClassTypeLookupHashcode("asd", 3)
        val firstSecClass = TestClassTypeLookupHashcode("asd2", 1)
        collection.addAll(firstClass, secondClass, thirdClass, firstSecClass)
        Assert.assertEquals(collection.size, 4)
        Assert.assertEquals(collection.getTypeRepresentativeFromTypeValue(1), firstClass.getInflaterFunction())
        Assert.assertEquals(collection.getTypeRepresentativeFromTypeValue(2), secondClass.getInflaterFunction())
        Assert.assertEquals(collection.getTypeRepresentativeFromTypeValue(3), thirdClass.getInflaterFunction())
        //the firstSecClass should be there, its just not the first type (1) that gets inserted.
        Assert.assertEquals(collection[3], firstSecClass)
    }

    @Test
    fun testGetAnItemFromTypeRemoveal() {
        val collection = TypeLookupCollectionRepresentive<TestClassTypeLookupHashcode<*>, String>()
        val firstClass = TestClassTypeLookupHashcode("asd", 1)
        val secondClass = TestClassTypeLookupHashcode("asd", 2)
        val thirdClass = TestClassTypeLookupHashcode("asd", 3)
        val firstSecClass = TestClassTypeLookupHashcode("asd2", 1)
        collection.addAll(firstClass, secondClass, thirdClass, firstSecClass)
        Assert.assertEquals(collection.size, 4)
        Assert.assertEquals(collection.getTypeRepresentativeFromTypeValue(1), firstClass.getInflaterFunction())
        collection.removeAt(0)
        Assert.assertEquals(collection.getTypeRepresentativeFromTypeValue(1), firstSecClass.getInflaterFunction())
        collection.remove(firstSecClass)
        Assert.assertEquals(collection.getTypeRepresentativeFromTypeValue(1), null)
    }

    @Test
    fun testPerformanceWorstCaseRemoval() {
        val collection = TypeLookupCollectionRepresentive<TestClassTypeLookupHashcode<*>, String>()
        collection.add(TestClassTypeLookupHashcode("asd", 1))
        collection.add(TestClassTypeLookupHashcode("asd", 1))
        collection.add(TestClassTypeLookupHashcode("asd", 1))
        val list = listOf(TestClassTypeLookupHashcode("2", 2)).repeateToSize(500_000)
        collection.add(TestClassTypeLookupHashcode("asd", 1))

        collection.removeAt(0) //warm up system.
        val baseLine = measureNanoTime {
            collection.removeAt(0) //good case , since the representative is just the next item. so even a regular traversal would work.
        }

        val worstCase = measureNanoTime {
            collection.removeAt(0) //worst case , since the representative is after 500_000 elements.
            // a forloop would be extremly slow
        }
        Assert.assertTrue(worstCase < baseLine * 2) //allow a margin of double the baseline.
        // this would still dictate a O(1) since O(1) * 2 = O(1)

    }


}