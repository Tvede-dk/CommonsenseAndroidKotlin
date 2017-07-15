package com.commonsense.android.kotlin.collections

import com.commonsense.android.kotlin.BaseRoboElectricTest
import com.commonsense.android.kotlin.testHelpers.assert
import com.commonsense.android.kotlin.testHelpers.assertEmpty
import com.commonsense.android.kotlin.testHelpers.assertNotNullApply
import com.commonsense.android.kotlin.testHelpers.assertSize
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

        a.add(TestData("a"), 0)
        a.differenceTo(b).diff.apply {
            size().assert(1)
            get(0).intersect.assertEmpty()
            get(0).outerSectA.assertSize(1)
            get(0).outerSectB.assertEmpty()
        }

        b.add(TestData("a"), 0)
        a.differenceTo(b).diff.apply {
            size().assert(1)
            get(0).intersect.assertSize(1)
            get(0).outerSectA.assertEmpty()
            get(0).outerSectB.assertEmpty()
        }

        b.add(TestData("b"), 50)
        a.add(TestData("c"), 20)

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

        a.add(TestData("1"), 0)
        a.assertSizeAndSections(1, 1)
        a[0, 0]!!.str.assert("1")

        a.add(TestData("2"), 0)
        a.assertSizeAndSections(2, 1)
        a[1, 0]!!.str.assert("2")

        a.add(TestData("11"), 1, 0)
        a.assertSizeAndSections(3, 1)
        a[1, 0]!!.str.assert("11")
        a[2, 0]!!.str.assert("2")
    }

    @Test
    fun testAdditionsMultipleSections() {
        val a = TypeSectionLookupRepresentative<TestData, String>()
        a.assertSizeAndSections(0, 0)

        a.add(TestData("1"), 0)

        a.add(TestData("2"), 0)

        a.add(TestData("22"), 0, 20)
        a.add(TestData("11"), 0, 10)

        a.addAll(listOf(TestData("a"), TestData("b")), 5)
        a.addAll(listOf(TestData("a"), TestData("b")), 15)


        a.assertSizeAndSections(8, 5)
        a.getSectionAt(0).assertNotNullApply {
            collection[0].str.assert("1")
            collection[1].str.assert("2")
        }
        a.getSectionAt(10).assertNotNullApply {
            collection[0].str.assert("11")
        }
        a.getSectionAt(20).assertNotNullApply {
            collection[0].str.assert("22")
        }


        a.getSectionAt(5).assertNotNullApply {
            collection[0].str.assert("a")
            collection[1].str.assert("b")

        }


        a.getSectionAt(15).assertNotNullApply {
            collection[0].str.assert("a")
            collection[1].str.assert("b")

        }


    }

    @Test
    fun testAddAll() {
        val a = TypeSectionLookupRepresentative<TestData, String>()
        a.assertSizeAndSections(0, 0)
    }


}

fun TypeSectionLookupRepresentative<*, *>.assertSizeAndSections(totalSize: Int, sections: Int) {
    size.assert(totalSize)
    sectionCount.assert(sections)
}