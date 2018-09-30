package com.commonsense.android.kotlin.system.datastructures

import android.util.*

/**
 * Created by Kasper Tvede on 29-07-2017.
 */

data class ListDiff<out T>(val intersect: List<T>, val outerSectA: List<T>, val outerSectB: List<T>, val isIndexConsidered: Boolean)

class TypeSectionCodeLookupDiff<T>(val diff: SparseArray<ListDiff<T>>)

fun <T : TypeHashCodeLookupRepresent<Rep>, Rep : Any> SectionLookupRep<T, Rep>.differenceTo(other: SectionLookupRep<T, Rep>): TypeSectionCodeLookupDiff<T> {
    val result = SparseArray<ListDiff<T>>()
    val thisMapped = this.mapAll { it }
    val otherMapped = other.mapAll { it }

    thisMapped.forEach { thisSection ->
        val otherSection = otherMapped.find { it.sectionIndexValue == thisSection.sectionIndexValue }
        if (otherSection == null) {
            result.put(thisSection.sectionIndexValue, ListDiff(listOf(), thisSection.collection.toList(), listOf(), true))
        } else {
            val interSect = thisSection.collection.intersect(otherSection.collection)
            val listA = thisSection.collection.filterNot { it in interSect }
            val listB = otherSection.collection.filterNot { it in interSect }
            result.put(thisSection.sectionIndexValue, ListDiff(interSect.toList(), listA, listB, false))
        }
    }

    otherMapped.forEach { otherSection ->
        val thisSection = thisMapped.find { it.sectionIndexValue == otherSection.sectionIndexValue }
        if (thisSection == null) {
            result.put(otherSection.sectionIndexValue, ListDiff(listOf(), listOf(), otherSection.collection.toList(), true))
        }
        //if not null then we have "processed it".
    }


    return TypeSectionCodeLookupDiff(result)
}