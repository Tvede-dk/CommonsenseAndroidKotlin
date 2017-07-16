package com.commonsense.android.kotlin.collections

import com.commonsense.android.kotlin.BaseRoboElectricTest
import com.commonsense.android.kotlin.extensions.collections.repeate
import com.commonsense.android.kotlin.extensions.collections.repeateToSize
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test
import kotlin.system.measureNanoTime

/**
 * Created by Kasper Tvede on 05-06-2017.
 */

class TestClassTypeLookupHashcode<out T>(val someData: T, val viewType: Int) : TypeHashCodeLookupRepresent<String> {
    override fun getCreatorFunction(): String {
        return ""
    }

    override fun getTypeValue(): Int = viewType
}


class TypeLookupCollectionRepresentiveTest : BaseRoboElectricTest() {
    @Test
    fun testRemove() {
        val collection = TypeLookupCollectionRepresentative<TestClassTypeLookupHashcode<*>, String>()
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
        val collection = TypeLookupCollectionRepresentative<TestClassTypeLookupHashcode<*>, String>()
        val firstClass = TestClassTypeLookupHashcode("asd", 1)
        val secondClass = TestClassTypeLookupHashcode("asd", 2)
        val thirdClass = TestClassTypeLookupHashcode("asd", 3)
        val firstSecClass = TestClassTypeLookupHashcode("asd2", 1)
        collection.addAll(firstClass, secondClass, thirdClass, firstSecClass)
        Assert.assertEquals(collection.size, 4)

        collection.removeAll(firstClass, firstSecClass, thirdClass)
        Assert.assertEquals(collection.size, 1)
        Assert.assertEquals(collection.getTypeRepresentativeFromTypeValue(1), null)
        Assert.assertEquals(collection.getTypeRepresentativeFromTypeValue(2), secondClass.getCreatorFunction())
        Assert.assertEquals(collection.getTypeRepresentativeFromTypeValue(3), null)
    }


    @Test
    fun clear() {
        val collection = TypeLookupCollectionRepresentative<TestClassTypeLookupHashcode<*>, String>()
        val firstClass = TestClassTypeLookupHashcode("asd", 1)
        collection.addAll(listOf(firstClass).repeate(10))
        Assert.assertEquals(collection.size, 10)
        collection.clear()
        Assert.assertEquals(collection.size, 0)
        Assert.assertEquals(collection.getTypeRepresentativeFromTypeValue(1), null)

    }

    @Test
    fun testInsertDifferentItems() {
        val collection = TypeLookupCollectionRepresentative<TestClassTypeLookupHashcode<*>, String>()
        val onlyClass = TestClassTypeLookupHashcode("asd", 1)
        collection.add(onlyClass)
        Assert.assertEquals(collection.size, 1)
        Assert.assertEquals(collection[0], onlyClass)
        Assert.assertEquals(collection.getTypeRepresentativeFromTypeValue(1), onlyClass.getCreatorFunction())

        val otherClass = TestClassTypeLookupHashcode("qwe", 2)
        collection.add(otherClass)
        Assert.assertEquals(collection.size, 2)
        Assert.assertEquals(collection[1], otherClass)
        Assert.assertEquals(collection.getTypeRepresentativeFromTypeValue(2), otherClass.getCreatorFunction())


    }

    @Test
    fun testGetAnItemFromType() {
        val collection = TypeLookupCollectionRepresentative<TestClassTypeLookupHashcode<*>, String>()
        val firstClass = TestClassTypeLookupHashcode("asd", 1)
        val secondClass = TestClassTypeLookupHashcode("asd", 2)
        val thirdClass = TestClassTypeLookupHashcode("asd", 3)
        val firstSecClass = TestClassTypeLookupHashcode("asd2", 1)
        collection.addAll(firstClass, secondClass, thirdClass, firstSecClass)
        Assert.assertEquals(collection.size, 4)
        Assert.assertEquals(collection.getTypeRepresentativeFromTypeValue(1), firstClass.getCreatorFunction())
        Assert.assertEquals(collection.getTypeRepresentativeFromTypeValue(2), secondClass.getCreatorFunction())
        Assert.assertEquals(collection.getTypeRepresentativeFromTypeValue(3), thirdClass.getCreatorFunction())
        //the firstSecClass should be there, its just not the first type (1) that gets inserted.
        Assert.assertEquals(collection[3], firstSecClass)
    }

    @Ignore
    @Test
    fun testGetAnItemFromTypeRemoveal() {
        val collection = TypeLookupCollectionRepresentative<TestClassTypeLookupHashcode<*>, String>()
        val firstClass = TestClassTypeLookupHashcode("asd", 1)
        val secondClass = TestClassTypeLookupHashcode("asd", 2)
        val thirdClass = TestClassTypeLookupHashcode("asd", 3)
        val firstSecClass = TestClassTypeLookupHashcode("asd2", 1)
        collection.addAll(firstClass, secondClass, thirdClass, firstSecClass)
        Assert.assertEquals(collection.size, 4)
        Assert.assertEquals(collection.getTypeRepresentativeFromTypeValue(1), firstClass.getCreatorFunction())
        collection.removeAt(0)
        Assert.assertEquals(collection.getTypeRepresentativeFromTypeValue(1), firstSecClass.getCreatorFunction())
        collection.remove(firstSecClass)
        Assert.assertEquals(collection.getTypeRepresentativeFromTypeValue(1), null)
    }

    @Test
    fun testReplaceIn() {
        val collection = TypeLookupCollectionRepresentative<TestClassTypeLookupHashcode<*>, String>()
        val firstItem = TestClassTypeLookupHashcode("asd0", 1)
        collection.add(firstItem)
        collection.add(TestClassTypeLookupHashcode("asd1", 1))
        collection.add(TestClassTypeLookupHashcode("asd2", 3))
        collection.add(TestClassTypeLookupHashcode("asd3", 2))
        val lastItem = TestClassTypeLookupHashcode("asd4", 4)
        collection.add(lastItem)

        collection.removeIn(1 until 4) //remove asd{1 until 4} [4 elements, 1 and 3 are inclusive]
        Assert.assertEquals(collection.size, 2)
        Assert.assertEquals(collection[0], firstItem)
        Assert.assertEquals(collection[1], lastItem)


    }

    @Ignore
    @Test
    fun testPerformanceWorstCaseRemoval() {
        val collection = TypeLookupCollectionRepresentative<TestClassTypeLookupHashcode<*>, String>()
        collection.add(TestClassTypeLookupHashcode("asd", 1))
        collection.add(TestClassTypeLookupHashcode("asd", 1))
        collection.addAll(listOf(TestClassTypeLookupHashcode("2", 2)).repeateToSize(500_000))
        collection.add(TestClassTypeLookupHashcode("asd", 1))

        collection.removeAt(0) //warm up system.
        val baseLine = measureNanoTime {
            collection.removeAt(0) //good case , since the representative is just the next item. so even a regular traversal would work.
        }

        val worstCase = measureNanoTime {
            collection.removeAt(0) //worst case , since the representative is after 500_000 elements.
            // a forloop would be extremly slow
        }
        Assert.assertTrue(worstCase < baseLine * 10) //allow a margin of double the baseline.
        // this would still dictate a O(1) since O(1) * 10 = O(1)

    }


}