package com.commonsense.android.kotlin.views.features

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

    private val operations: List<SectionTransactionCommando<T>>

    private val adapter: AbstractDataBindingRecyclerAdapter<T>

    private var isApplied = false

    private constructor(operations: List<SectionTransactionCommando<T>>,
                        adapter: AbstractDataBindingRecyclerAdapter<T>) {
        this.operations = operations
        this.adapter = adapter
    }

    fun applyTransaction() {
        if (isApplied) {
            //TODO should throw or log ??
            return
        }
        isApplied = true
        operations.forEach {
            it.applyOperation.invoke(adapter)
        }
    }

    fun resetTransaction() {
        if (!isApplied) {
            //TODO should throw or log ??
            return
        }
        isApplied = false
        operations.reversed().forEach {
            it.resetOperation.invoke(adapter)
        }
    }

    class Builder<T : IRenderModelItem<*, *>>(val adapter: AbstractDataBindingRecyclerAdapter<T>) {

        private val operations = mutableListOf<SectionTransactionCommando<T>>()

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
                = SectionRecyclerTransaction(operations, adapter)
    }


}
