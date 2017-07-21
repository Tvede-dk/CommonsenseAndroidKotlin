package com.commonsense.android.kotlin.views.features

import com.commonsense.android.kotlin.base.EmptyFunction
import com.commonsense.android.kotlin.base.extensions.collections.invokeEachWith
import com.commonsense.android.kotlin.base.patterns.ToggleBoolean
import com.commonsense.android.kotlin.views.databinding.adapters.AbstractDataBindingRecyclerAdapter
import com.commonsense.android.kotlin.views.databinding.adapters.IRenderModelItem

/**
 * Created by Kasper Tvede on 21-07-2017.
 */
typealias SectionOperation<T> = AbstractDataBindingRecyclerAdapter<T>.() -> Unit

private class SectionTransactionCommando<T : IRenderModelItem<*, *>>(
        val applyOperation: SectionOperation<T>,
        val resetOperation: SectionOperation<T>)

class SectionRecyclerTransaction<T : IRenderModelItem<*, *>> {

    private val applyTransactions: List<SectionOperation<T>>

    private val resetTransactions: List<SectionOperation<T>>

    private val adapter: AbstractDataBindingRecyclerAdapter<T>

    private val isApplied = ToggleBoolean(false)

    private var oldSize = 0

    private val allowExternalModifications: Boolean

    private constructor(applyTransactions: List<SectionOperation<T>>,
                        resetTransactions: List<SectionOperation<T>>,
                        adapter: AbstractDataBindingRecyclerAdapter<T>,
                        allowExternalModifications: Boolean) {

        this.adapter = adapter
        oldSize = adapter.itemCount
        this.allowExternalModifications = allowExternalModifications
        this.applyTransactions = applyTransactions
        this.resetTransactions = resetTransactions
    }

    fun apply() = isApplied.ifFalse {
        performTransactions(applyTransactions)
    }

    fun applySafe(ifNotSafe: EmptyFunction? = null) {
        if (canPerformTransaction()) {
            apply()
        } else {
            ifNotSafe?.invoke()
        }
    }

    fun resetSafe(ifNotSafe: EmptyFunction? = null) {
        if (canPerformTransaction()) {
            reset()
        } else {
            ifNotSafe?.invoke()
        }
    }

    fun reset() = isApplied.ifTrue {
        performTransactions(resetTransactions)
    }

    fun canPerformTransaction(): Boolean =
            adapter.itemCount == oldSize || allowExternalModifications

    private fun performTransactions(opertaions: List<SectionOperation<T>>) {
        if (!canPerformTransaction()) {
            throw RuntimeException("External changes are not permitted on the adapter for this transaction;\r\n" +
                    "if this is expected, set allow external modifications to true.")
            //WARNING this is super dangerous, we are potentially unable to do our transaction.
        }
        opertaions.invokeEachWith(adapter)
        oldSize = adapter.itemCount
    }

    class Builder<T : IRenderModelItem<*, *>>(val adapter: AbstractDataBindingRecyclerAdapter<T>) {

        private val operations = mutableListOf<SectionTransactionCommando<T>>()

        var allowExternalModifications = false

        private fun addOperation(applyOperation: SectionOperation<T>, resetOperation: SectionOperation<T>) {
            operations.add(SectionTransactionCommando(applyOperation, resetOperation))
        }

        fun hideSection(sectionIndex: Int) {
            addOperation({ hideSection(sectionIndex) }, { showSection(sectionIndex) })
        }

        fun showSection(sectionIndex: Int) {
            addOperation({ showSection(sectionIndex) }, { hideSection(sectionIndex) })
        }

        fun addItems(items: List<T>, inSection: Int) {
            addOperation({ addAll(items, inSection) }, { removeItems(items, inSection) })
        }

        fun removeItems(items: List<T>, inSection: Int) {
            addOperation({ removeItems(items, inSection) }, { addAll(items, inSection) })
        }

        fun addItem(item: T, inSection: Int) {
            addOperation({ add(item, inSection) }, { removeItem(item, inSection) })
        }

        fun removeItem(item: T, inSection: Int) {
            addOperation({ removeItem(item, inSection) }, { add(item, inSection) })
        }

        fun addItemAt(item: T, row: Int, inSection: Int) {
            addOperation({ insert(item, row, inSection) }, { this.removeAt(row, inSection) })
        }

        fun removeItemAt(item: T, row: Int, inSection: Int) {
            addOperation({ removeAt(row, inSection) }, { insert(item, row, inSection) })
        }


        fun build(): SectionRecyclerTransaction<T>
                = SectionRecyclerTransaction(
                operations.map { it.applyOperation },
                operations.reversed().map { it.resetOperation },
                adapter,
                allowExternalModifications)
    }


}
