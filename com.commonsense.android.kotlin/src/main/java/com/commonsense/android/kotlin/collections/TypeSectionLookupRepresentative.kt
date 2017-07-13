package com.commonsense.android.kotlin.collections

import android.support.annotation.IntRange
import android.util.SparseArray
import com.commonsense.android.kotlin.android.logging.L
import com.commonsense.android.kotlin.extensions.collections.*
import length
import map

/**
 * Created by kasper on 05/07/2017.
 */

data class TypeSection<T>(
        val sectionIndexValue: Int,
        var isIgnored: Boolean = false,
        val collection: MutableList<T> = mutableListOf()) {
    val size: Int
        get() = collection.size

    /**
     * if this section is to be ignored. (in ui terms, hidden for example)
     */

    val visibileCount: Int
        get() = isIgnored.map(0, size)
//
//    var header: T? = null
//
//    var footer: T? = null

}

class TypeSectionLookupRepresentative<T : TypeHashCodeLookupRepresent<Rep>, out Rep : Any> {


    private val lookup = TypeRepresentative<T, Rep>()

    private val data: SparseArray<TypeSection<T>> = SparseArray()

    @IntRange(from = 0)
    private var cachedSize: Int = 0

    val size
        @IntRange(from = 0)
        get() = cachedSize

    val sectionCount
        @IntRange(from = 0)
        get () = data.size()

    private inline fun <T> updateCacheForSection(sectionIndex: Int, crossinline action: () -> T): T {

        val before = getSectionAt(sectionIndex)?.isIgnored
        val sizeBefore = getSectionAt(sectionIndex)?.visibileCount ?: 0

        val actionResult = action()

        val sizeAfter = getSectionAt(sectionIndex)?.visibileCount ?: 0
        val after = getSectionAt(sectionIndex)?.isIgnored
        cachedSize += (sizeAfter - sizeBefore)

        L.error("Section [$sectionIndex]", "size updated with: ${(sizeAfter - sizeBefore)}; ignore : $before -> $after")
        return actionResult
    }

    private inline fun ensureSection(@IntRange(from = 0) atSection: Int,
                                     crossinline function: () -> Unit) {
        addSectionIfMissing(atSection)
        function()
    }

    private fun addSectionIfMissing(@IntRange(from = 0) atSection: Int) {
        if (data[atSection, null] == null) {
            data.put(atSection, TypeSection(atSection))
        }
    }

    fun add(item: T, @IntRange(from = 0) atSection: Int) = ensureSection(atSection) {
        updateCacheForSection(atSection) {
            data.get(atSection).collection.add(item)
            lookup.add(item)
        }
    }


    fun addAll(items: Collection<T>, @IntRange(from = 0) atSection: Int) = ensureSection(atSection) {
        updateCacheForSection(atSection) {
            data.get(atSection).collection.addAll(items)
            lookup.addAll(items)
        }
    }

    fun removeItem(item: T, @IntRange(from = 0) atSection: Int) {
        updateCacheForSection(atSection) {
            val didRemove = data[atSection]?.collection?.remove(item) ?: false
            if (didRemove) {
                lookup.remove(item)
            }
        }
    }

    fun removeItems(items: List<T>, @IntRange(from = 0) atSection: Int) {
        items.forEach {
            this.removeItem(it, atSection)
        }
    }


    fun getItem(@IntRange(from = 0) atRow: Int, @IntRange(from = 0) atSection: Int): T? =
            data.get(atSection)?.collection?.getSafe(atRow)

    private fun IndexPathIsValid(@IntRange(from = 0) atRow: Int, @IntRange(from = 0) atSection: Int): Boolean =
            getItem(atRow, atSection) != null

    operator fun get(@IntRange(from = 0) atRow: Int, @IntRange(from = 0) atSection: Int): T? = getItem(atRow, atSection)

    operator fun get(index: IndexPath): T? = getItem(index.row, index.section)


    fun getTypeRepresentativeFromTypeValue(type: Int): Rep? =
            lookup.getTypeRepresentativeFromTypeValue(type)

    fun add(item: T, @IntRange(from = 0) atRow: Int, @IntRange(from = 0) atSection: Int) = ensureSection(atSection) {
        updateCacheForSection(atSection) {
            data[atSection].collection.add(atRow, item)
            lookup.add(item)
        }
    }

    /**
     * returns what have changed. (the diff).
     */
    fun clearAndSetSection(items: List<T>, @IntRange(from = 0) atSection: Int): SectionUpdate {
        val removed = clearSection(atSection)
        addAll(items, atSection)
        if (removed == null) {
            return SectionUpdate(calculateLocationForSection(atSection), null, null)
        }
        val changedEnd = removed.start + minOf(removed.length, items.size) + 1
        val changedRange = removed.start until changedEnd
        L.warning("typeSection", "at section : $atSection, removed : ${(removed.length > items.size)}")
        return if (removed.length > items.size) {
            SectionUpdate(changedRange, null, changedEnd until removed.endInclusive + 1)
        } else {
            SectionUpdate(changedRange, changedEnd until items.size + 1, null)
        }

    }

    fun replace(newItem: T, @IntRange(from = 0) position: Int, @IntRange(from = 0) inSection: Int)
            = ensureSection(inSection) {
        data[inSection].collection.replace(newItem, position)
    }

    fun clear() {
        data.clear()
        lookup.clear()
        cachedSize = 0
    }


    fun addAll(items: Collection<T>, @IntRange(from = 0) startPosition: Int, @IntRange(from = 0) atSection: Int)
            = ensureSection(atSection) {
        updateCacheForSection(atSection) {
            data[atSection].collection.addAll(startPosition, items)
            lookup.addAll(items)
        }
    }

    fun indexToPath(@IntRange(from = 0) position: Int): IndexPath? {
        //naive implementation

        var currentPosition = position
        for (dataIndex in 0 until data.size()) {
            val section = data.keyAt(dataIndex)
            val item = data[section]
            if (item.isIgnored) {
                continue//skip ignored entries.
            }

            if (currentPosition < item.size) {
                return IndexPath(currentPosition, section)
            }
            currentPosition -= item.size
        }
        return null
    }


    fun calculateLocationForSection(@IntRange(from = 0) sectionIndex: Int): kotlin.ranges.IntRange? {
        addSectionIfMissing(sectionIndex)
        val dataItems = data.toList(sectionIndex).filter { !it.value.isIgnored }
        if (dataItems.isEmpty()) {
            return null
        }
        val start = dataItems.dropLast(1).sumBy { it.value.size }
        val end = dataItems.last().value.size + start
        return start..end
    }

    fun removeAt(@IntRange(from = 0) row: Int, @IntRange(from = 0) inSection: Int): Boolean {
        if (data[inSection]?.collection?.isIndexValid(row) != true) {
            return false
        }
        val item = updateCacheForSection(inSection) {
            return@updateCacheForSection data[inSection]?.collection?.removeAt(row)
        }
        item?.let(lookup::remove)
        return item != null
    }

    fun removeInRange(range: kotlin.ranges.IntRange, atSection: Int): Boolean {
        addSectionIfMissing(atSection)
        val section = data[atSection]
        return updateCacheForSection(atSection) {
            val didRemove = section.collection.removeAll(range)
            return@updateCacheForSection didRemove
        }
    }

    fun indexOf(newItem: T, atSection: Int): Int {
        addSectionIfMissing(atSection)
        return data[atSection].collection.indexOf(newItem)
    }

    fun ignoreSection(sectionIndex: Int): kotlin.ranges.IntRange? {
        addSectionIfMissing(sectionIndex)
        val location = calculateLocationForSection(sectionIndex)
        if (data[sectionIndex].isIgnored) {
            return null
        }
        updateCacheForSection(sectionIndex) {
            data[sectionIndex].isIgnored = true
        }
        return location
    }

    /**
     * Inverse of ignore section.
     */
    fun acceptSection(@IntRange(from = 0) sectionIndex: Int): kotlin.ranges.IntRange? {
        addSectionIfMissing(sectionIndex)
        if (!data[sectionIndex].isIgnored) {
            return null
        }
        updateCacheForSection(sectionIndex) {
            data[sectionIndex].isIgnored = false
        }
        return calculateLocationForSection(sectionIndex)
    }

    fun toggleSectionVisibility(@IntRange(from = 0) sectionIndex: Int) = ensureSection(sectionIndex) {
        data[sectionIndex].isIgnored = !data[sectionIndex].isIgnored
    }

    fun getSectionLocation(@IntRange(from = 0) sectionIndex: Int): kotlin.ranges.IntRange? =
            calculateLocationForSection(sectionIndex)

    fun clearSection(@IntRange(from = 0) atSection: Int): kotlin.ranges.IntRange? {
        addSectionIfMissing(atSection)
        @IntRange(from = 0)
        val location = calculateLocationForSection(atSection) ?: return null
        updateCacheForSection(atSection) {
            data[atSection].collection.clear()
        }
        return location
    }

    fun <U> mapNoIgnored(converter: (List<T>) -> List<U>): List<TypeSection<U>> =
            mapAll(converter).filter { !it.isIgnored }

    fun <U> mapAll(converter: (List<T>) -> List<U>): List<TypeSection<U>> =
            data.toList().map {
                TypeSection(it.key, it.value.isIgnored, it.value.collection.let(converter)
                        .toMutableList())
            }


    fun getSectionAt(sectionIndex: Int): TypeSection<T>? = data[sectionIndex]

    fun setAllSections(sections: List<TypeSection<T>>) {
        cachedSize = sections.sumBy { it.visibileCount }
        data.clear()
        sections.forEach { data.put(it.sectionIndexValue, it) }
        lookup.clear()
        sections.forEach { it.collection.forEach(lookup::add) }
    }

}

data class IndexPath(@IntRange(from = 0) val row: Int, @IntRange(from = 0) val section: Int)

data class SectionUpdate(val changes: kotlin.ranges.IntRange?,
                         val optAdded: kotlin.ranges.IntRange?,
                         val optRemoved: kotlin.ranges.IntRange?)
