package com.commonsense.android.kotlin.collections

import com.commonsense.android.kotlin.BaseRoboElectricTest
import com.commonsense.android.kotlin.testHelpers.*
import length
import org.junit.Test

/**
 * Created by Kasper Tvede on 15-07-2017.
 */
data class TestData(val str: String) : TypeHashCodeLookupRepresent<String> {
    override fun getCreatorFunction(): String = str

    override fun getTypeValue(): Int = str.hashCode()

}

class TypeSectionLookupRepresentativeTest : BaseRoboElectricTest() {


    @Test
    fun testDiff() {
        val a = TypeSectionLookupRepresentative<TestData, String>()
        val b = TypeSectionLookupRepresentative<TestData, String>()
        val diff = a.differenceTo(b).diff
        diff.size().assert(0, "no diff on empty")

        a.add(TestData("a"), 0).apply {
            inSection.assert(0)
            rawRow.assert(0)
        }
        a.differenceTo(b).diff.apply {
            size().assert(1)
            get(0).intersect.assertEmpty()
            get(0).outerSectA.assertSize(1)
            get(0).outerSectB.assertEmpty()
        }

        b.add(TestData("a"), 0).apply {
            inSection.assert(0)
            rawRow.assert(0)
        }
        a.differenceTo(b).diff.apply {
            size().assert(1)
            get(0).intersect.assertSize(1)
            get(0).outerSectA.assertEmpty()
            get(0).outerSectB.assertEmpty()
        }

        b.add(TestData("b"), 50).apply {
            inSection.assert(0)
            rawRow.assert(1)
        }
        a.add(TestData("c"), 20).apply {
            inSection.assert(0)
            rawRow.assert(1)
        }

        a.differenceTo(b).diff.apply {
            size().assert(3)
            get(0).intersect.assertSize(1)
            get(20).intersect.assertEmpty()
            get(50).intersect.assertEmpty()

        }

    }

    @Test
    fun testAdditionsSingleSection() {
        val a = TypeSectionLookupRepresentative<TestData, String>()
        a.assertSizeAndSections(0, 0)

        a.add(TestData("1"), 0).assertNotNullApply {
            inSection.assert(0)
            rawRow.assert(0)
        }
        a.assertSizeAndSections(1, 1)
        a[0, 0]!!.str.assert("1")

        a.add(TestData("2"), 0).assertNotNullApply {
            inSection.assert(1)
            rawRow.assert(1)
        }
        a.assertSizeAndSections(2, 1)
        a[1, 0]!!.str.assert("2")

        a.insert(TestData("11"), 1, 0).assertNotNullApply {
            rawRow.assert(1)
            inSection.assert(1)
        }

        a.insert(TestData("11"), 1, 1).assertNull("cannot create a section from insertion")

        a.assertSizeAndSections(3, 1)
        a[1, 0]!!.str.assert("11")
        a[2, 0]!!.str.assert("2")
    }

    @Test
    fun testAdditionsMultipleSections() {
        val a = TypeSectionLookupRepresentative<TestData, String>()
        a.assertSizeAndSections(0, 0)

        a.add(TestData("1"), 0).assertNotNullApply {
            inSection.assert(0)
            rawRow.assert(0)
        }

        a.insert(TestData("2"), 1, 0).assertNotNullApply {
            inSection.assert(1)
            rawRow.assert(1)
        }

        a.add(TestData("22"), 20).assertNotNullApply {
            inSection.assert(0)
            rawRow.assert(2)
        }
        a.add(TestData("11"), 10).assertNotNullApply {
            inSection.assert(0)
            rawRow.assert(2)
        }
        a.addAll(listOf(TestData("a"), TestData("b")), 5).assertNotNullApply {
            inSection.assert(0 until 2)
            inRaw.assert(2 until 4)

        }
        a.addAll(listOf(TestData("a"), TestData("b")), 15).assertNotNullApply {
            inRaw.assert(5 until 7)
            inSection.assert(0 until 2)

        }

        a.assertSizeAndSections(8, 5)
        a.sectionAt(0).assertNotNullApply {
            collection[0].str.assert("1")
            collection[1].str.assert("2")
        }
        a.sectionAt(10).assertNotNullApply {
            collection[0].str.assert("11")
        }
        a.sectionAt(20).assertNotNullApply {
            collection[0].str.assert("22")
        }

        a.sectionAt(5).assertNotNullApply {
            collection[0].str.assert("a")
            collection[1].str.assert("b")

        }

        a.sectionAt(15).assertNotNullApply {
            collection[0].str.assert("a")
            collection[1].str.assert("b")

        }
    }

    @Test
    fun testRemoveSimple() {
        val a = TypeSectionLookupRepresentative<TestData, String>()
        a.assertSizeAndSections(0, 0)
        val tempTestData = TestData("0")
        val tempTestData2 = TestData("1")
        a.addAll((0 until 50).map { TestData(it.toString()) }, 0).assertNotNullApply {
            inRaw.assert(0 until 50)
            inSection.assert(0 until 50)
        }
        a.assertSizeAndSections(50, 1)

        a.apply {
            removeItem(tempTestData, 0).assertNotNullApply {
                rawRow.assert(0)
                inSection.assert(0)
            }
            assertSizeAndSections(49, 1)
            get(0, 0).assertNotNullApply {
                str.assert("1")
            }
        }

        a.apply {
            removeAt(0, 0).assertNotNullApply {
                rawRow.assert(0)
                inSection.assert(0)
            }
            assertSizeAndSections(48, 1)
            get(0, 0).assertNotNullApply {
                str.assert("2")
            }
        }

        a.apply {
            removeAt(47, 0).assertNotNullApply {
                rawRow.assert(47)
                inSection.assert(47)
            }
            assertSizeAndSections(47, 1)
            get(46, 0).assertNotNullApply {
                str.assert("48") // we had [0..., 48,49] now we removed the last, so 48
            }
        }

        a.apply {
            removeItem(tempTestData2, 0).assertNull()
            assertSizeAndSections(47, 1, "removing removed item should not remove anything")
            get(0, 0).assertNotNullApply {
                str.assert("2")
            }
        }

        a.apply {
            removeItems(listOf(tempTestData, tempTestData, tempTestData2), 0).assertSize(0)
            assertSizeAndSections(47, 1, "removing already removed items should not remove anything")
            get(0, 0).assertNotNullApply {
                str.assert("2")
            }
            get(46, 0).assertNotNullApply {
                str.assert("48")
            }
        }

        a.apply {
            removeInRange(0 until 12, 0).assertNotNullApply {
                inRaw.assert(0 until 12)
                inSection.assert(0 until 12)
            }
            assertSizeAndSections(35, 1)
            get(0, 0).assertNotNullApply {
                str.assert("14")
            }
        }

        a.apply {
            removeInRange(10 until 35, 0).assertNotNullApply {
                inRaw.assert(10 until 35)
                inSection.assert(10 until 35)
            }
            assertSizeAndSections(10, 1)
        }

        a.apply {
            removeInRange(5 until 8, 0).assertNotNullApply {
                inRaw.assert(5 until 8)
                inSection.assert(5 until 8)
            }
            assertSizeAndSections(7, 1)
        }


    }

    @Test
    fun testRemoveSections() {
        val a = TypeSectionLookupRepresentative<TestData, String>()
        a.assertSizeAndSections(0, 0)
        //create section [0,10,20,30,40,50] with 50 elements in each.
        (0 until 51 step 10).forEach { section ->
            a.addAll((0 until 50).map { TestData(it.toString()) }, section).assertNotNullApply {
                inSection.assert(0 until 50)
                inRaw.assert(section / 10 * 50 until (section / 10 + 1) * 50)
            }
        }
        a.assertSizeAndSections(50 * 6, 6)

        //section 50
        a.removeSection(50).assertNotNullApply {
            inSection.assert(0 until 50)
            inRaw.assert(5 * 50 until 6 * 50)
        }
        a.assertSizeAndSections(50 * 5, 5)

        //section 40
        a.removeInRange(0 until 50, 40).assertNotNullApply {
            inSection.assert(0 until 50)
            inRaw.assert(4 * 50 until 5 * 50)
        }
        a.assertSizeAndSections(50 * 4, 4, "should clear up sections, and not leave empty once behind.")
        a.sectionAt(40).assertNull()

        //section 30
        a.removeAt(0, 30).assertNotNullApply {
            inSection.assert(0)
            rawRow.assert(3 * 50)
        }
        a.assertSizeAndSections((50 * 4) - 1, 4, "remove one item should not fuck stuff up.")
        a.removeInRange(0 until (50 - 2), 30).assertNotNullApply {
            inSection.assert(0 until (50 - 2))
            inRaw.assert(3 * 50 until ((4 * 50) - 2))
        }
        a.assertSizeAndSections((50 * 4) - 49, 4, "one item in section 30, should still hold up")
        a.removeAt(0, 30).assertNotNullApply {
            inSection.assert(0)
            rawRow.assert(3 * 50)
        }
        a.assertSizeAndSections(50 * 3, 3, "removing last item in section should cause it to disappear.")
        a.sectionAt(30).assertNull()


        //section 20
        a.removeInRange(0 until 49, 20).assertNotNullApply {
            inSection.assert(0 until 49)
            inRaw.assert(2 * 50 until (3 * 50 - 1))
        }
        a[0, 20].assertNotNullApply {
            a.removeItem(this, 20).assertNotNullApply {
                inSection.assert(0)
                rawRow.assert(2 * 50)
            }
        }
        a.assertSizeAndSections(50 * 2, 2)
        a.sectionAt(20).assertNull()

        a.sectionAt(10).assertNotNullApply {
            a.removeItems(this.collection.dropLast(1), 10).assertNotNullApply {
                assertSize(49)
            }
            a.assertSizeAndSections(50 + 1, 2)
            a.removeItems(listOf(this.collection.last()), 10).assertNotNullApply {
                assertSize(1)
            }
            a.assertSizeAndSections(50, 1)
        }

        a.sectionAt(0).assertNotNullApply {
            a.removeItems(this.collection.toList(), 0).assertNotNullApply {
                assertSize(50)
            }
            a.assertSizeAndSections(0, 0)
        }


    }


    @Test
    fun testReplace() {
        val a = TypeSectionLookupRepresentative<TestData, String>()
        a.assertSizeAndSections(0, 0)

        a.add(TestData("a"), 0).assertNotNullApply {
            inSection.assert(0)
            rawRow.assert(0)
        }
        a[0, 0].assertNotNullApply {
            this.str.assert("a")
        }
        a.assertSizeAndSections(1, 1)
        a.replace(TestData("1"), 0, 0).assertNotNullApply {
            inSection.assert(0)
            rawRow.assert(0)
        }
        a.assertSizeAndSections(1, 1)

        a[0, 0].assertNotNullApply {
            this.str.assert("1")
        }

    }

    @Test
    fun testSectionOperations() {
        val a = TypeSectionLookupRepresentative<TestData, String>()

        a.assertSizeAndSections(0, 0)
        a.setSection((0 until 50).map { TestData(it.toString()) }, 5).optAdded.assertNotNullApply {
            inSection.length.assert(50, "should have added 50 elements")
            inSection.start.assert(0, "should start at 0")
            inSection.endInclusive.assert(49, "end should be expected 49 (inclusive), as the length")
        }
        a.assertSizeAndSections(50, 1)
        a.setSection((0 until 100).map { TestData(it.toString()) }, 0).optAdded.assertNotNullApply {
            inSection.length.assert(100, "should have added 100 elements")
            inSection.start.assert(0, "should start at 0")
            inSection.endInclusive.assert(99, "end should be expected 99 (inclusive), as the length -1 ")
        }
        a.assertSizeAndSections(150, 2)

        a.setSection((0 until 10).map { TestData(it.toString()) }, 10).optAdded.assertNotNullApply {
            inSection.length.assert(10, "should have added 10 elements")
            inSection.start.assert(0, "should start at 0")
            inSection.endInclusive.assert(9, "end should be expected 9 (inclusive), as the length -1 ")
        }
        a.assertSizeAndSections(160, 3)




        a.setSection(listOf(), 5).assertNotNullApply {

        }

        a.assertSizeAndSections(110, 2, "clearing a section should remove it.")

        a.setSection(listOf(), 10).assertNotNullApply {

        }
        a.assertSizeAndSections(100, 1, "clearing a section should remove it.")

        a.setSection(listOf(), 0).assertNotNullApply {

        }
        a.assertSizeAndSections(0, 0, "clearing a section should remove it.")


        //so insert and clear "work", now lets try changes. there are a few combinations
        // full change
        // change and remove
        // change and insert


        a.addAll((0 until 50).map { TestData(it.toString()) }, 5).assertNotNullApply {
            inSection.assert(0 until 50)
            inRaw.assert(0 until 50)
        }
        a.assertSizeAndSections(50, 1)
        a.setSection((0 until 100).map { TestData(it.toString()) }, 0).assertNotNullApply {

        }
        a.assertSizeAndSections(150, 2)
        a.setSection((0 until 10).map { TestData(it.toString()) }, 10).assertNotNullApply {

        }
        a.assertSizeAndSections(160, 3)


        // full change
        a.setSection((50 until 100).map { TestData(it.toString()) }, 5).assertNotNullApply {

        }
        a.assertSizeAndSections(160, 3)


        //change and remove
        a.setSection((20 until 50).map { TestData(it.toString()) }, 0).assertNotNullApply {

        }
        a.assertSizeAndSections(90, 3) //we just removed 70 and changed 30

        //change and insert
        a.setSection((100 until 120).map { TestData(it.toString()) }, 10).assertNotNullApply {

        }
        a.assertSizeAndSections(100, 3) //we just added 10 and changed 10


    }

}

fun TypeSectionLookupRepresentative<*, *>.assertSizeAndSections(totalSize: Int, sections: Int, message: String = "") {
    size.assert(totalSize, message)
    sectionCount.assert(sections, message)
}