@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.system.datastructures

import android.support.annotation.IntRange
import android.util.*
import com.commonsense.android.kotlin.base.debug.prettyStringContent
import com.commonsense.android.kotlin.base.extensions.*
import com.commonsense.android.kotlin.base.extensions.collections.*
import kotlin.Pair


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
        val collection: MutableList<T> = mutableListOf()
) {
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

    override fun toString(): String {
        return toPrettyString()
    }

    fun toPrettyString(): String {
        return "TypeSection state:" + listOf(
                "\tsize = $size",
                "\tvisibleCount = $visibleCount",
                "\tisIgnored = $isIgnored"

        ).prettyStringContent()
    }
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
    @IntRange(from = 0)
    private var cachedSize: Int = 0
    //</editor-fold>

    private val cachedIndex = SectionIndexCache()

    //<editor-fold desc="Sizes">
    /**
     * The total number of items in this
     */
    val size
        @IntRange(from = 0)
        get() = cachedSize

    /**
     * The total number of sections in this (including invisible sections)
     */
    val sectionCount
        @IntRange(from = 0)
        get() = data.size()

    //</editor-fold>

    //<editor-fold desc="Add functions">
    /**
     * Adds the given item tot he given section
     * @param item T the item to add / append
     * @param inSection Int into this section (sparse index)
     * @return SectionLocation? the resulting update if any was there (if this section is ignored then this becomes null)
     */
    fun add(item: T, @IntRange(from = 0) inSection: Int): SectionLocation? {
        val sectionUpdate = addSectionIfMissing(inSection)
        updateCacheForSection(inSection) {
            data[inSection].collection.add(item)
            lookup.add(item)
        }
        if (isSectionIgnored(inSection)) {
            return null
        }
        return SectionLocation(sectionUpdate.inRaw.last + 1, sectionUpdate.inSection.last + 1)
    }

    private fun isSectionIgnored(inSection: Int): Boolean =
            data.get(inSection, null)?.isIgnored ?: false

    fun insert(item: T, @IntRange(from = 0) atRow: Int, @IntRange(from = 0) inSection: Int): SectionLocation? {
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
        return SectionLocation(sectionUpdate.inRaw.first + atRow, atRow)
    }


    fun addAll(items: Collection<T>, @IntRange(from = 0) inSection: Int): SectionUpdate? {
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

    fun insertAll(items: Collection<T>, @IntRange(from = 0) startPosition: Int, @IntRange(from = 0) inSection: Int): SectionUpdate? {
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
    fun removeItem(item: T, @IntRange(from = 0) inSection: Int): SectionLocation? {
        val section = calculateSectionLocation(inSection) ?: return null
        return updateCacheForSection(inSection) {
            val indexOf = data[inSection].collection.indexOf(item)
            return@updateCacheForSection if (indexOf == -1) {
                null
            } else {
                data[inSection].collection.removeAt(indexOf)
                lookup.remove(item)
                isSectionIgnored(inSection).map(null,
                        SectionLocation(section.inRaw.first + indexOf, indexOf))
            }
        }
    }

    fun removeAt(@IntRange(from = 0) row: Int, @IntRange(from = 0) inSection: Int): SectionLocation? {
        val sectionLocation = calculateSectionLocation(inSection)
        if (sectionLocation == null || isIndexValidInSection(row, inSection) != true) {
            return null
        }
        return updateCacheForSection(inSection) {
            val item = data[inSection].collection.removeAt(row)
            lookup.remove(item)
            isSectionIgnored(inSection).map(null,
                    SectionLocation(sectionLocation.inRaw.first + row, row))
        }
    }

    private fun isIndexValidInSection(row: Int, inSection: Int) =
            data[inSection]?.collection?.isIndexValid(row)


    fun removeItems(items: List<T>, @IntRange(from = 0) inSection: Int): List<SectionLocation> =
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
        return SectionUpdate((section.inRaw.first + range.first) until
                (section.inRaw.first + range.largest + 1), range)
    }
    //</editor-fold>

    fun replace(newItem: T, @IntRange(from = 0) atRow: Int, @IntRange(from = 0) inSection: Int): SectionLocation? {
        val sectionLocation = addSectionIfMissing(inSection)
        if (!data[inSection].collection.isIndexValid(atRow)) {
            return null
        }
        data[inSection].collection.replace(newItem, atRow)
        if (isSectionIgnored(inSection)) {
            return null
        }
        return SectionLocation(sectionLocation.inRaw.first + atRow, atRow)
    }

    fun getTypeRepresentativeFromTypeValue(type: Int): Rep? =
            lookup.getTypeRepresentativeFromTypeValue(type)


    fun clear() {
        data.clear()
        lookup.clear()
        cachedIndex.invalidate()
        cachedSize = 0
    }

    fun indexToPath(@IntRange(from = 0) position: Int): IndexPath? {
        return cachedIndex.lookup(position, data)
    }


    private fun indexPathIsValid(@IntRange(from = 0) atRow: Int, @IntRange(from = 0) inSection: Int): Boolean =
            getItem(atRow, inSection) != null

    fun indexOf(newItem: T, inSection: Int): SectionLocation? {
        val locationOfSection = calculateSectionLocation(inSection) ?: return null
        val indexInSection = data[inSection].collection.indexOf(newItem)
        if (indexInSection == -1) {
            return null
        }
        return SectionLocation(locationOfSection.inRaw.first + indexInSection, indexInSection)
    }


    private fun getItem(@IntRange(from = 0) atRow: Int, @IntRange(from = 0) inSection: Int): T? =
            data.get(inSection)?.collection?.getSafe(atRow)

    //<editor-fold desc="Section ignorance">
    fun ignoreSection(@IntRange(from = 0) sectionIndex: Int): SectionUpdate? {
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
    fun acceptSection(@IntRange(from = 0) sectionIndex: Int): SectionUpdate? {
        if (!sectionExists(sectionIndex) || !data[sectionIndex].isIgnored) {
            return null
        }
        updateCacheForSection(sectionIndex) {
            data[sectionIndex].isIgnored = false
        }
        return calculateSectionLocation(sectionIndex)
    }

    fun toggleSectionVisibility(@IntRange(from = 0) sectionIndex: Int): SectionUpdate? =
            setSectionVisibility(sectionIndex, !isSectionIgnored(sectionIndex))

    fun setSectionVisibility(@IntRange(from = 0) sectionIndex: Int, visible: Boolean): SectionUpdate? {
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
    fun setSection(items: List<T>, @IntRange(from = 0) inSection: Int): SectionUpdates? {
        val wasVisible = !isSectionIgnored(inSection)
        val removed = clearSection(inSection)
        val added = addAll(items, inSection)
        setSectionVisibility(inSection, wasVisible)
        if (removed == null) {
            return SectionUpdates(null, added, removed)
        }

        val addedSafe = added ?: return null

        val inSectionChangedEnd = minOf(removed.inSection.length, addedSafe.inSection.length)

        val startOffsetRaw = addedSafe.inRaw.first
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


    fun getSectionLocation(@IntRange(from = 0) sectionIndex: Int): SectionUpdate? =
            calculateSectionLocation(sectionIndex)

    fun clearSection(@IntRange(from = 0) inSection: Int): SectionUpdate? {
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
        cachedIndex.invalidate()
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

    operator fun get(@IntRange(from = 0) atRow: Int, @IntRange(from = 0) inSection: Int): T? = getItem(atRow, inSection)

    operator fun get(index: IndexPath): T? = getItem(index.row, index.section)

    operator fun get(index: Int): T? = indexToPath(index)?.let(::get)
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

        cachedIndex.invalidate()
        //todo move this around into something like "onChanged" and then set this variable there instead.
//        isPrecomputedUpToDate = false

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

    private fun addSectionIfMissing(@IntRange(from = 0) inSection: Int): SectionUpdate {
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

    override fun toString(): String {
        return toPrettyString()
    }

    fun toPrettyString(): String {
        return "Section lookup rep state:  " + listOf(
                lookup.toPrettyString(),
                "cached Size: $cachedSize",
                "precomputed lookup = $cachedIndex",
                data.toPrettyString()
        ).prettyStringContent()
    }

}

/**
 * super simple and dirty way to compute the number of visible sections.
 * @receiver SparseArray<TypeSection<T>>
 * @return Int
 */
private fun <T> SparseArray<TypeSection<T>>.visibleSections(): Int {
    var result = 0
    forEach {
        if (it.isNotEmptyOrInvisible) {
            result += 1
        }
    }
    return result
}

/**
 * tells if this typesection is either empty or invisible
 * @receiver TypeSection<T>
 * @return Boolean
 */
private val <T> TypeSection<T>.isNotEmptyOrInvisible: Boolean
    get() = visibleCount > 0

private val <T> TypeSection<T>.isEmptyOrInvisible: Boolean
    get() = !isNotEmptyOrInvisible

/**
 * An index path (the location of an item) in a sectionized container
 * @property row Int the row index in the given section
 * @property section Int the section index (sparse)
 */
data class IndexPath(@IntRange(from = 0) val row: Int,
                     @IntRange(from = 0) val section: Int)

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

/**
 * A cache between a "dataSet" and a "relative position"
 * such as between a single index and a section with rows in it.
 */
class SectionIndexCache {
    /**
     * key = section
     * value = "raw index start" for this container
     */
    private var sectionMapping = SparseIntArray()

    /**
     * Tries to recompute the mappings iff we are either invalid or the forceUpdate is true
     * @param data TypeSection<T> the data to reflect the section mapping for
     * @param forceUpdate Boolean if true will update even if we have not been marked invalid.
     */
    fun <T> recompute(data: SparseArray<TypeSection<T>>, forceUpdate: Boolean = false) {
        if (isValid && !forceUpdate) {
            return
        }
        rebuildMapping(data)
        isValid = true
    }

    /**
     * Rebuilds our mapping
     * @param data TypeSection<T>
     */
    private fun <T> rebuildMapping(data: SparseArray<TypeSection<T>>) {
        sectionMapping.clear()
        var currentSize = 0
        data.forEachIndexed { _, value, _ ->
            if (value.isNotEmptyOrInvisible) {
                currentSize += value.visibleCount
                sectionMapping.append(value.sectionIndexValue, currentSize)
            }
        }
    }

    /**
     * converts a raw index to a IndexPath iff possible
     * @param rawIndex Int the raw index to lookup
     * @param data TypeSection<T> the data to rebuild the mapping of if we are invalid
     * @return IndexPath? null if outside of the data set, else an IndexPath there to.
     */
    fun <T> lookup(rawIndex: Int, data: SparseArray<TypeSection<T>>): IndexPath? {
        recompute(data, false)
        val didFind = sectionMapping.findContainingSectionAndStartIndex(rawIndex)
        val (key, value) = didFind ?: return null
        return IndexPath(rawIndex - value, key)
    }

    /**
     * Marks this as not up to date / invalid
     */
    fun invalidate() {
        isValid = false
        sectionMapping.clear()
    }

    /**
     * if true we are up to date / valid
     */
    var isValid: Boolean = true
        private set

    /**
     * if true, we are invalid / not up to date.
     */
    val isInvalid: Boolean
        get() = !isValid

    /**
     *
     * @receiver SparseIntArray
     * @param rawIndex Int
     * @return Pair<Int, Int>? the key is the size, the value is the section
     */
    private fun SparseIntArray.findContainingSectionAndStartIndex(rawIndex: Int): Pair<Int, Int>? {
        val index = binarySearch { _: Int, value: Int, index: Int ->
            val from = previousValueOr(index, 0)
            rawIndex.compareToRange(from, value - 1) //make it exclusive
        } ?: return null
        val prevIndex = indexOfKey(index.first)
        val from = previousValueOr(prevIndex, 0)
        return Pair(index.first, from)//first is section, from is the start of the section.
    }

}

