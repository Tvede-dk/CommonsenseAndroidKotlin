@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.system.datastructures

import android.util.*
import com.commonsense.android.kotlin.base.extensions.*
import com.commonsense.android.kotlin.base.extensions.collections.*


/**
 * A typed section and all the properties associated with a section
 * @param T The containing type.
 */
data class TypeSection<T>(
        val sectionIndexValue: Int,
        /**
         * if this section is to be ignored. (in ui terms, hidden for example)
         */
        var isIgnored: Boolean = false,
        /**
         *  The content of this section
         */
        val collection: MutableList<T> = mutableListOf()) {

    /**
     * retrieves the real size of the section  (ignores the isIgnored flag).
     */
    val size: Int
        get() = collection.size

    /**
     * The size with respect to the isIgnored flag.
     */
    val visibleCount: Int
        get() = isIgnored.map(0, size)
}

/**
 * A container for having sections with very complex types that can represent" a feature
 * @param T : TypeHashCodeLookupRepresent<Rep>
 * @param Rep : Any
 */
class SectionLookupRep<T : TypeHashCodeLookupRepresent<Rep>, out Rep : Any> {

    //<editor-fold desc="Internal data">
    /**
     * The mapper for the type representatives.
     */
    private val lookup = TypeRepresentative<T, Rep>()

    /**
     * The raw underlying data structure
     */
    private val data: SparseArray<TypeSection<T>> = SparseArray()

    /**
     * The total number of items in this, cached
     */
    @androidx.annotation.IntRange(from = 0)
    private var cachedSize: Int = 0
    //</editor-fold>

    //<editor-fold desc="Sizes">
    /**
     * The total number of items in this
     */
    val size
        @androidx.annotation.IntRange(from = 0)
        get() = cachedSize

    /**
     * The total number of sections in this
     */
    val sectionCount
        @androidx.annotation.IntRange(from = 0)
        get () = data.size()

    //</editor-fold>


    /**
     * Its a "mapping" / lookup list of absolute sizes
     * should make queries from raw position to IndexPath's a simple O(log_2(sectionsCount)
     * works by computing the start of a given section (in raw)
     */
    private var preComputedLookup: IntArray = intArrayOf()
    /**
     * Tells if the precomputed lookup is up to date.
     */
    private var isPrecomputedUpToDate = false


    //<editor-fold desc="Add functions">
    /**
     * Adds the given item tot he given section
     * @param item T the item to add / append
     * @param inSection Int into this section (sparse index)
     * @return SectionLocation? the resulting update if any was there (if this section is ignored then this becomes null)
     */
    fun add(item: T, @androidx.annotation.IntRange(from = 0) inSection: Int): SectionLocation? {
        val sectionUpdate = addSectionIfMissing(inSection)
        updateCacheForSection(inSection) {
            data[inSection].collection.add(item)
            lookup.add(item)
        }
        if (isSectionIgnored(inSection)) {
            return null
        }
        return SectionLocation(sectionUpdate.inRaw.endInclusive + 1, sectionUpdate.inSection.endInclusive + 1)
    }

    private fun isSectionIgnored(inSection: Int): Boolean =
            data.get(inSection, null)?.isIgnored ?: false

    fun insert(item: T, @androidx.annotation.IntRange(from = 0) atRow: Int, @androidx.annotation.IntRange(from = 0) inSection: Int): SectionLocation? {
        if (!sectionExists(inSection) || !data[inSection].collection.isIndexValidForInsert(atRow)) {
            return null
        }
        val sectionUpdate = addSectionIfMissing(inSection)

        updateCacheForSection(inSection) {
            data[inSection].collection.add(atRow, item)
            lookup.add(item)
        }
        if (isSectionIgnored(inSection)) {
            return null
        }
        return SectionLocation(sectionUpdate.inRaw.start + atRow, atRow)
    }


    fun addAll(items: Collection<T>, @androidx.annotation.IntRange(from = 0) inSection: Int): SectionUpdate? {
        val section = addSectionIfMissing(inSection)
        updateCacheForSection(inSection) {
            data.get(inSection).collection.addAll(items)
            lookup.addAll(items)
        }
        if (isSectionIgnored(inSection)) {
            return null
        }
        return SectionUpdate(section.inRaw.largest until section.inRaw.largest + items.size,
                section.inSection.largest until section.inSection.largest + items.size)
    }

    fun insertAll(items: Collection<T>, @androidx.annotation.IntRange(from = 0) startPosition: Int, @androidx.annotation.IntRange(from = 0) inSection: Int): SectionUpdate? {
        if (!sectionExists(inSection) || !data[inSection].collection.isIndexValidForInsert(startPosition)) {
            return null
        }
        val section = addSectionIfMissing(inSection)
        updateCacheForSection(inSection) {
            data[inSection].collection.addAll(startPosition, items)
            lookup.addAll(items)
        }
        if (isSectionIgnored(inSection)) {
            return null
        }
        return SectionUpdate(section.inRaw.largest + startPosition until section.inRaw.largest + startPosition + items.size,
                section.inSection.largest + startPosition until section.inSection.largest + startPosition + items.size)
    }
    //</editor-fold>

    //<editor-fold desc="Remove functions">
    fun removeItem(item: T, @androidx.annotation.IntRange(from = 0) inSection: Int): SectionLocation? {
        val section = calculateSectionLocation(inSection) ?: return null
        return updateCacheForSection(inSection) {
            val indexOf = data[inSection].collection.indexOf(item)
            return@updateCacheForSection if (indexOf == -1) {
                null
            } else {
                data[inSection].collection.removeAt(indexOf)
                lookup.remove(item)
                isSectionIgnored(inSection).map(null,
                        SectionLocation(section.inRaw.start + indexOf, indexOf))
            }
        }
    }

    fun removeAt(@androidx.annotation.IntRange(from = 0) row: Int, @androidx.annotation.IntRange(from = 0) inSection: Int): SectionLocation? {
        val sectionLocation = calculateSectionLocation(inSection)
        if (sectionLocation == null || isIndexValidInSection(row, inSection) != true) {
            return null
        }
        return updateCacheForSection(inSection) {
            val item = data[inSection].collection.removeAt(row)
            lookup.remove(item)
            isSectionIgnored(inSection).map(null,
                    SectionLocation(sectionLocation.inRaw.start + row, row))
        }
    }

    private fun isIndexValidInSection(row: Int, inSection: Int) =
            data[inSection]?.collection?.isIndexValid(row)


    fun removeItems(items: List<T>, @androidx.annotation.IntRange(from = 0) inSection: Int): List<SectionLocation> =
            items.mapNotNull { removeItem(it, inSection) }


    fun removeInRange(range: kotlin.ranges.IntRange, inSection: Int): SectionUpdate? {
        val section = calculateSectionLocation(inSection)
        if (section == null || !data[inSection].collection.isRangeValid(range)) {
            return null
        }
        updateCacheForSection(inSection) {
            lookup.removeAll(data[inSection].collection.subList(range))
            data[inSection].collection.removeAll(range)
        }
        if (isSectionIgnored(inSection)) {
            return null
        }
        return SectionUpdate((section.inRaw.start + range.start) until
                (section.inRaw.start + range.largest + 1), range)
    }
    //</editor-fold>

    fun replace(newItem: T, @androidx.annotation.IntRange(from = 0) atRow: Int, @androidx.annotation.IntRange(from = 0) inSection: Int): SectionLocation? {
        val sectionLocation = addSectionIfMissing(inSection)
        if (!data[inSection].collection.isIndexValid(atRow)) {
            return null
        }
        data[inSection].collection.replace(newItem, atRow)
        if (isSectionIgnored(inSection)) {
            return null
        }
        return SectionLocation(sectionLocation.inRaw.start + atRow, atRow)
    }

    fun getTypeRepresentativeFromTypeValue(type: Int): Rep? =
            lookup.getTypeRepresentativeFromTypeValue(type)


    fun clear() {
        data.clear()
        lookup.clear()
        cachedSize = 0
    }


    fun indexToPath(@androidx.annotation.IntRange(from = 0) position: Int): IndexPath? {
        computeLookup()

        val index = preComputedLookup.binarySearch { item: Int, index: Int ->
            val from = preComputedLookup.previousValueOr(index, 0)
            position.compareToRange(from, item - 1)
        }
        return index?.let {
            val from = preComputedLookup.previousValueOr(it, 0)
            val key = data.keyAt(it)
            IndexPath(position - from, key)
        }
    }

    private fun computeLookup() {
        if (isPrecomputedUpToDate) {
            return
        }
        var counter = 0
        val size = data.size()
        //do not allocate more than necessarily. so if we have the right size, then use that.
        val newLookup = if (preComputedLookup.size == size) {
            preComputedLookup
        } else {
            IntArray(size)
        }
        size.forEach {
            val key = data.keyAt(it)
            counter += data[key].visibleCount
            newLookup[it] = counter
        }
        preComputedLookup = newLookup
        isPrecomputedUpToDate = true
    }

    private fun indexPathIsValid(@androidx.annotation.IntRange(from = 0) atRow: Int, @androidx.annotation.IntRange(from = 0) inSection: Int): Boolean =
            getItem(atRow, inSection) != null

    fun indexOf(newItem: T, inSection: Int): SectionLocation? {
        val locationOfSection = calculateSectionLocation(inSection) ?: return null
        val indexInSection = data[inSection].collection.indexOf(newItem)
        if (indexInSection == -1) {
            return null
        }
        return SectionLocation(locationOfSection.inRaw.start + indexInSection, indexInSection)
    }


    private fun getItem(@androidx.annotation.IntRange(from = 0) atRow: Int, @androidx.annotation.IntRange(from = 0) inSection: Int): T? =
            data.get(inSection)?.collection?.getSafe(atRow)

    //<editor-fold desc="Section igorance">
    fun ignoreSection(@androidx.annotation.IntRange(from = 0) sectionIndex: Int): SectionUpdate? {
        if (!sectionExists(sectionIndex) || data[sectionIndex].isIgnored) {
            return null
        }
        val location = calculateSectionLocation(sectionIndex)
        updateCacheForSection(sectionIndex) {
            data[sectionIndex].isIgnored = true
        }
        return location
    }

    /**
     * Inverse of ignore section.
     */
    fun acceptSection(@androidx.annotation.IntRange(from = 0) sectionIndex: Int): SectionUpdate? {
        if (!sectionExists(sectionIndex) || !data[sectionIndex].isIgnored) {
            return null
        }
        updateCacheForSection(sectionIndex) {
            data[sectionIndex].isIgnored = false
        }
        return calculateSectionLocation(sectionIndex)
    }

    fun toggleSectionVisibility(@androidx.annotation.IntRange(from = 0) sectionIndex: Int): SectionUpdate? =
            setSectionVisibility(sectionIndex, !isSectionIgnored(sectionIndex))

    fun setSectionVisibility(@androidx.annotation.IntRange(from = 0) sectionIndex: Int, visible: Boolean): SectionUpdate? {
        return if (visible) {
            acceptSection(sectionIndex)
        } else {
            ignoreSection(sectionIndex)
        }
    }
//</editor-fold>

//<editor-fold desc="Section opertations">

    /**
     * returns what have changed. (the diff).
     */
    fun setSection(items: List<T>, @androidx.annotation.IntRange(from = 0) inSection: Int): SectionUpdates? {
        val wasVisible = !isSectionIgnored(inSection)
        val removed = clearSection(inSection)
        val added = addAll(items, inSection)
        setSectionVisibility(inSection, wasVisible)
        if (removed == null) {
            return SectionUpdates(null, added, removed)
        }

        val addedSafe = added ?: return null

        val inSectionChangedEnd = minOf(removed.inSection.length, addedSafe.inSection.length)

        val startOffsetRaw = addedSafe.inRaw.start
        val changedEndOffSetRaw = startOffsetRaw + inSectionChangedEnd
        //if the list is empty, then no change can occur => null.
        val changedRange = items.isEmpty().map(null,
                SectionUpdate(startOffsetRaw until changedEndOffSetRaw, 0 until inSectionChangedEnd))

        val removedLength = removed.inSection.length
        val addedLength = added.inSection.length

        if (isSectionIgnored(inSection)) {
            return null
        }

        return when {
            removedLength > addedLength -> {
                val removedUpdate = SectionUpdate(
                        changedEndOffSetRaw until startOffsetRaw + removedLength,
                        inSectionChangedEnd until removedLength)
                SectionUpdates(changedRange, null, removedUpdate)
            }
            addedLength > removedLength -> {
                val addedUpdate = SectionUpdate(
                        changedEndOffSetRaw until startOffsetRaw + addedLength,
                        inSectionChangedEnd until addedLength
                )
                SectionUpdates(changedRange, addedUpdate, null)
            }
            else -> SectionUpdates(changedRange, null, null)
        }
    }


    fun getSectionLocation(@androidx.annotation.IntRange(from = 0) sectionIndex: Int): SectionUpdate? =
            calculateSectionLocation(sectionIndex)

    fun clearSection(@androidx.annotation.IntRange(from = 0) inSection: Int): SectionUpdate? {
        if (data[inSection, null] == null) { //missing section => null.
            return null
        }
        val location = calculateSectionLocation(inSection)
        updateCacheForSection(inSection) {
            data.remove(inSection)
        }
        return isSectionIgnored(inSection).map(ifTrue = null, ifFalse = location)
    }


    fun setAllSections(sections: List<TypeSection<T>>) {
        cachedSize = sections.sumBy { it.visibleCount }
        data.clear()
        sections.forEach { data.put(it.sectionIndexValue, it) }
        lookup.clear()
        sections.forEach { it.collection.forEach(lookup::add) }
    }

    fun removeSection(sectionIndex: Int): SectionUpdate? {
        if (data.get(sectionIndex, null) == null) {
            return null
        }
        val section = data[sectionIndex]
        val location = calculateSectionLocation(sectionIndex)
        data.remove(sectionIndex)
        cachedSize -= section.size

        return location
    }

    /**  */
    fun sectionAt(sectionIndex: Int): TypeSection<T>? = data[sectionIndex]

    private fun sectionExists(sectionIndex: Int): Boolean = data[sectionIndex, null] != null
//</editor-fold>

//<editor-fold desc="Operators">

    /**  */
    operator fun get(@androidx.annotation.IntRange(from = 0) atRow: Int, @androidx.annotation.IntRange(from = 0) inSection: Int): T? = getItem(atRow, inSection)

    /**  */
    operator fun get(index: IndexPath): T? = getItem(index.row, index.section)
//</editor-fold>

    //<editor-fold desc="cache handling">
    private inline fun <T> updateCacheForSection(sectionIndex: Int, crossinline action: () -> T): T {
        val sizeBefore = sectionAt(sectionIndex)?.visibleCount ?: 0
        val actionResult = action()
        val sizeAfter = sectionAt(sectionIndex)?.visibleCount ?: 0
        cachedSize += (sizeAfter - sizeBefore)
        if (sizeAfter == 0 && sectionAt(sectionIndex)?.isIgnored != true) {
            data.remove(sectionIndex)
        }

        //todo move this around into something like "onChanged" and then set this variable there instead.
        isPrecomputedUpToDate = false

        return actionResult
    }
//</editor-fold>

    //<editor-fold desc="Map functions">
    fun <U> mapNoIgnored(converter: (List<T>) -> List<U>): List<TypeSection<U>> =
            mapAll(converter).filter { !it.isIgnored }

    fun <U> mapAll(converter: (List<T>) -> List<U>): List<TypeSection<U>> =
            data.toList().map {
                TypeSection(it.key, it.value.isIgnored, it.value.collection.let(converter)
                        .toMutableList())
            }
//</editor-fold>

//<editor-fold desc="location calculations">

    private fun addSectionIfMissing(@androidx.annotation.IntRange(from = 0) inSection: Int): SectionUpdate {
        if (!sectionExists(inSection)) {
            data.put(inSection, TypeSection(inSection))
        }
        return calculateSectionLocation(inSection) ?: SectionUpdate(0 until 0, 0 until 0)
    }

    //TODO verify this performance wise after adding the preComputedLookup
    private fun calculateSectionLocation(sectionIndex: Int): SectionUpdate? {
        if (!sectionExists(sectionIndex)) {
            return null
        }
        val dataItems = data.toList(sectionIndex - 1).filter { !it.value.isIgnored }
        val collectionSize = data[sectionIndex].collection.size
        val start = dataItems.sumBy { it.value.size }
        val end = start + collectionSize
        return SectionUpdate(start until end, 0 until collectionSize)
    }

//</editor-fold>

}

/**
 * An index path (the location of an item) in a sectionized container
 * @property row Int the row index in the given section
 * @property section Int the section index (sparse)
 */
data class IndexPath(@androidx.annotation.IntRange(from = 0) val row: Int,
                     @androidx.annotation.IntRange(from = 0) val section: Int)

/**
 * Describes an update, both in "mapped" / sparse and raw terms.
 * @property inRaw IntRange the raw (no section concept)
 * @property inSection IntRange the section concept (section -> row)
 */
data class SectionUpdate(val inRaw: kotlin.ranges.IntRange, val inSection: kotlin.ranges.IntRange)

/**
 * A location inside of a section
 * @property rawRow Int the raw row index (not sparse / mapped)
 * @property inSection Int the section this is in.
 */
data class SectionLocation(val rawRow: Int, val inSection: Int)

/**
 * Describes changes, additions, and removed in a given section operation.
 * @property changes SectionUpdate? contains all the changed ranges
 * @property optAdded SectionUpdate? contains all the added items ranges
 * @property optRemoved SectionUpdate? contains all the removed items ranges
 */
data class SectionUpdates(val changes: SectionUpdate?,
                          val optAdded: SectionUpdate?,
                          val optRemoved: SectionUpdate?)


