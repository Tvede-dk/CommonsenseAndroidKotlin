package com.commonsense.android.kotlin.base.extensions.collections

import com.commonsense.android.kotlin.base.extensions.forEach
import com.commonsense.android.kotlin.test.*
import org.junit.*
import org.junit.jupiter.api.Test

/**
 * Created by Kasper Tvede on 21-05-2018.
 * Purpose:
 */
class CollectionExtensionsKtTest {

    @Test
    fun isIndexValid() {
        val collection: MutableCollection<String> = mutableListOf()
        collection.isIndexValid(0).assertFalse()
        collection.isIndexValid(-1).assertFalse()
        collection.add("test")
        collection.isIndexValid(0).assertTrue()
        collection.isIndexValid(-1).assertFalse()

        10.forEach {
            collection.add("")
        }
        for (i in 0 until 11) {
            collection.isIndexValid(i).assertTrue()
        }
        collection.isIndexValid(12).assertFalse()

    }

    @Test
    fun isIndexValidForInsert() {
        val collection: MutableCollection<String> = mutableListOf()
        collection.isIndexValidForInsert(0).assertTrue()
        collection.isIndexValidForInsert(-1).assertFalse()
        collection.add("test")
        collection.isIndexValidForInsert(0).assertTrue()
        collection.isIndexValidForInsert(1).assertTrue()
        collection.isIndexValidForInsert(2).assertFalse()
        collection.isIndexValidForInsert(12).assertFalse()
    }

    @Test
    fun getSafe() {
        val collection: MutableCollection<String> = mutableListOf()
        collection.getSafe(-1).assertNull()
        collection.getSafe(0).assertNull("collection is empty.")
        collection.getSafe(1).assertNull()
        collection.add("test")
        collection.getSafe(-1).assertNull()
        collection.getSafe(0).assertNotNullAndEquals("test")
        collection.getSafe(1).assertNull()
        collection.add("1")
        collection.add("2")
        collection.add("3")
        collection.getSafe(3).assertNotNullAndEquals("3")
        collection.getSafe(4).assertNull()
        collection.getSafe(2).assertNotNullAndEquals("2")
        collection.getSafe(1).assertNotNullAndEquals("1")


    }

    @Ignore
    @Test
    fun categorizeInto() {

    }

    @Ignore
    @Test
    fun categorize() {

    }

    @Ignore
    @Test
    fun repeate() {


    }

    @Test
    fun repeateToSize() {
        val collection: MutableList<Int> = mutableListOf()
        collection.repeateToSize(50).apply {
            size.assert(0, "repeating nothing is wrong and gives nothing")
        }

        collection.add(42)
        collection.repeateToSize(50).apply {
            size.assert(50)

        }

        collection.add(42)
        collection.repeateToSize(50).apply {
            size.assert(50)

        }

    }

    @Ignore
    @Test
    fun isRangeValid() {

    }

    @Ignore
    @Test
    fun subList() {

    }

    @Ignore
    @Test
    fun limitToSize() {

    }

    @Ignore
    @Test
    fun invokeEachWith() {

    }

    @Ignore
    @Test
    fun forEachNotNull() {

    }

    @Ignore
    @Test
    fun invokeEachWith1() {
    }

    @Ignore
    @Test
    fun invokeEachWith2() {
    }

    @Ignore
    @Test
    fun invokeEachWith3() {
    }

    @Ignore
    @Test
    fun invokeEachWith4() {
    }

    @Ignore
    @Test
    fun invokeEachWith5() {
    }
}