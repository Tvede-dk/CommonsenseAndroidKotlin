package com.commonsense.android.kotlin.system.datastructures

import com.commonsense.android.kotlin.base.extensions.collections.repeateToSize
import com.commonsense.android.kotlin.test.*
import org.junit.Ignore
import org.junit.Test
import kotlin.system.measureNanoTime

/**
 * Created by Kasper Tvede on 20-07-2017.
 */

/**
 * Created by Kasper Tvede on 05-06-2017.
 */

class TestClassTypeLookupHashcode<out T>(val someData: T, val viewType: Int) : TypeHashCodeLookupRepresent<String> {
    override fun getCreatorFunction(): String = ""

    override fun getTypeValue(): Int = viewType
}


class TypeLookupCollectionRepresentiveTest : BaseRoboElectricTest() {
    @Test
    fun testRemove() {
        val collection = TypeLookupCollectionRepresentative<TestClassTypeLookupHashcode<*>, String>()
        val firstClass = TestClassTypeLookupHashcode("asd", 1)
        collection.addAll(listOf(firstClass).repeateToSize(10))
        collection.size.assert(10)
        collection.remove(collection[5]!!)
        collection.remove(collection[7]!!)
        collection.remove(collection[1]!!)
        collection.size.assert(7)
    }

    @Test
    fun testRemoveAll() {
        val collection = TypeLookupCollectionRepresentative<TestClassTypeLookupHashcode<*>, String>()
        val firstClass = TestClassTypeLookupHashcode("asd", 1)
        val secondClass = TestClassTypeLookupHashcode("asd", 2)
        val thirdClass = TestClassTypeLookupHashcode("asd", 3)
        val firstSecClass = TestClassTypeLookupHashcode("asd2", 1)
        collection.addAll(firstClass, secondClass, thirdClass, firstSecClass)
        collection.size.assert(4)
        collection.removeAll(firstClass, firstSecClass, thirdClass)

        collection.size.assert(1)
        collection.getTypeRepresentativeFromTypeValue(1).assertNull()
        collection.getTypeRepresentativeFromTypeValue(2).assertNotNullApply { this.assert(secondClass.getCreatorFunction()) }
        collection.getTypeRepresentativeFromTypeValue(3).assertNull()
    }


    @Test
    fun clear() {
        val collection = TypeLookupCollectionRepresentative<TestClassTypeLookupHashcode<*>, String>()
        val firstClass = TestClassTypeLookupHashcode("asd", 1)
        collection.addAll(listOf(firstClass).repeateToSize(10))
        collection.size.assert(10)
        collection.clear()
        collection.size.assert(0)
        collection.getTypeRepresentativeFromTypeValue(1).assertNull()

    }

    @Test
    fun testInsertDifferentItems() {
        val collection = TypeLookupCollectionRepresentative<TestClassTypeLookupHashcode<*>, String>()
        val onlyClass = TestClassTypeLookupHashcode("asd", 1)
        collection.add(onlyClass)
        collection.size.assert(1)
        collection[0].assertNotNullAndEquals(onlyClass)
        collection.getTypeRepresentativeFromTypeValue(1).assertNotNullAndEquals(onlyClass.getCreatorFunction())

        val otherClass = TestClassTypeLookupHashcode("qwe", 2)
        collection.add(otherClass)
        collection.size.assert(2)
        collection[1].assertNotNullAndEquals(otherClass)
        collection.getTypeRepresentativeFromTypeValue(2).assertNotNullAndEquals(otherClass.getCreatorFunction())

    }

    @Test
    fun testGetAnItemFromType() {
        val collection = TypeLookupCollectionRepresentative<TestClassTypeLookupHashcode<*>, String>()
        val firstClass = TestClassTypeLookupHashcode("asd", 1)
        val secondClass = TestClassTypeLookupHashcode("asd", 2)
        val thirdClass = TestClassTypeLookupHashcode("asd", 3)
        val firstSecClass = TestClassTypeLookupHashcode("asd2", 1)
        collection.addAll(firstClass, secondClass, thirdClass, firstSecClass)
        collection.size.assert(4)
        collection.getTypeRepresentativeFromTypeValue(1).assertNotNullAndEquals(firstClass.getCreatorFunction())
        collection.getTypeRepresentativeFromTypeValue(2).assertNotNullAndEquals(secondClass.getCreatorFunction())
        collection.getTypeRepresentativeFromTypeValue(3).assertNotNullAndEquals(thirdClass.getCreatorFunction())
        //the firstSecClass should be there, its just not the first type (1) that gets inserted.
        collection[3].assertNotNullAndEquals(firstSecClass)
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
        collection.size.assert(4)
        collection.getTypeRepresentativeFromTypeValue(1).assertNotNullAndEquals(firstClass.getCreatorFunction())
        collection.removeAt(0)
        collection.getTypeRepresentativeFromTypeValue(1).assertNotNullAndEquals(firstClass.getCreatorFunction())
        collection.remove(firstSecClass)
        collection.getTypeRepresentativeFromTypeValue(1).assertNull()

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
        collection.size.assert(2)
        (collection[0] == firstItem).assert(true)

        (collection[1] == lastItem).assert(true)
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
        (worstCase < baseLine * 10).assert(true) //allow a margin of double the baseline.
        // this would still dictate a O(1) since O(1) * 10 = O(1)

    }


}