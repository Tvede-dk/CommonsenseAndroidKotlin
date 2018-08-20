package com.commonsense.android.kotlin.system.datastructures

import android.support.annotation.IntRange
import android.util.SparseArray
import com.commonsense.android.kotlin.base.extensions.collections.*

/**
 * Created by kasper on 05/07/2017.
 */


data class TypeSection<T>(
        val sectionIndexValue: Int,
        /**
         * if this section is to be ignored. (in ui terms, hidden for example)
         */
        var isIgnored: Boolean = false,
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
 *
 *
 */
class SectionLookupRep<T : TypeHashCodeLookupRepresent<Rep>, out Rep : Any> {

    //<editor-fold desc="Internal data">
    private val lookup = TypeRepresentative<T, Rep>()

    private val data: SparseArray<TypeSection<T>> = SparseArray()

    @IntRange(from = 0)
    private var cachedSize: Int = 0
    //</editor-fold>

    //<editor-fold desc="Sizes">
    val size
        @IntRange(from = 0)
        get() = cachedSize

    val sectionCount
        @IntRange(from = 0)
        get () = data.size()
    //</editor-fold>


    //<editor-fold desc="Add functions">
    fun add(item: T, @IntRange(from = 0) inSection: Int): SectionLocation? {
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
        return SectionLocation(sectionUpdate.inRaw.start + atRow, atRow)
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
                        SectionLocation(section.inRaw.start + indexOf, indexOf))
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
                    SectionLocation(sectionLocation.inRaw.start + row, row))
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
        return SectionUpdate((section.inRaw.start + range.start) until
                (section.inRaw.start + range.largest + 1), range)
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
        return SectionLocation(sectionLocation.inRaw.start + atRow, atRow)
    }

    fun getTypeRepresentativeFromTypeValue(type: Int): Rep? =
            lookup.getTypeRepresentativeFromTypeValue(type)


    fun clear() {
        data.clear()
        lookup.clear()
        cachedSize = 0
    }


    //this is btw O(Section) which is bad with many sections. can be optimized to O(log_2(sections)) then, even a million sections would be "good". (Log_2(10^6) ~ 20
    //the idea; keep a sparseArray of "accumulated" sizes; then we can binary search that.
    // the Accumulated calculation will be "O(section)" amortised but as the bright person have observed, that is the same speed as it is now...
    //TODO hmm, convert this ?
    fun indexToPath(@IntRange(from = 0) position: Int): IndexPath? {
        //naive implementation
        var currentPosition = position
        var result: IndexPath? = null
        //TODO , mabye cache the list implementation. or something.

        data.findFirst { _: Int, value: TypeSection<T> ->
            when {
                //ignore ignored.
                value.isIgnored -> false
                //found it
                currentPosition < value.size -> {
                    result = IndexPath(currentPosition, value.sectionIndexValue)
                    true
                }
                //subtract (continue search)
                else -> {
                    currentPosition -= value.size
                    false
                }
            }
        }
        return result
//        val toIterate = data.toList().filterNot { it.value.isIgnored }
//        toIterate.find {
//            if (currentPosition < it.value.size) {
//                result = IndexPath(currentPosition, it.value.sectionIndexValue)
//                true
//            } else {
//                currentPosition -= it.value.size
//                false
//            }
//        }
//        return result
    }

    private fun indexPathIsValid(@IntRange(from = 0) atRow: Int, @IntRange(from = 0) inSection: Int): Boolean =
            getItem(atRow, inSection) != null

    fun indexOf(newItem: T, inSection: Int): SectionLocation? {
        val locationOfSection = calculateSectionLocation(inSection) ?: return null
        val indexInSection = data[inSection].collection.indexOf(newItem)
        if (indexInSection == -1) {
            return null
        }
        return SectionLocation(locationOfSection.inRaw.start + indexInSection, indexInSection)
    }


    private fun getItem(@IntRange(from = 0) atRow: Int, @IntRange(from = 0) inSection: Int): T? =
            data.get(inSection)?.collection?.getSafe(atRow)

    //<editor-fold desc="Section igorance">
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
    operator fun get(@IntRange(from = 0) atRow: Int, @IntRange(from = 0) inSection: Int): T? = getItem(atRow, inSection)

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

data class IndexPath(@IntRange(from = 0) val row: Int,
                     @IntRange(from = 0) val section: Int)

data class SectionUpdate(val inRaw: kotlin.ranges.IntRange, val inSection: kotlin.ranges.IntRange)

data class SectionLocation(val rawRow: Int, val inSection: Int)

data class SectionUpdates(val changes: SectionUpdate?,
                          val optAdded: SectionUpdate?,
                          val optRemoved: SectionUpdate?)


