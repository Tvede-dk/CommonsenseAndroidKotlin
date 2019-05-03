//@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")
//
//package com.commonsense.android.kotlin.views.features
//
//import com.commonsense.android.kotlin.base.*
//import com.commonsense.android.kotlin.base.extensions.collections.*
//import com.commonsense.android.kotlin.base.patterns.*
//import com.commonsense.android.kotlin.views.databinding.adapters.*
//
///**
// * Created by Kasper Tvede on 21-07-2017.
// */
//typealias SectionOperation<T> = DataBindingRecyclerAdapter<T>.() -> Unit
//
//typealias FunctionAdapterBoolean<T> = (DataBindingRecyclerAdapter<T>) -> Boolean
//
//private class SectionTransactionCommando<T : IRenderModelItem<*, *>>(
//        val applyOperation: SectionOperation<T>,
//        val resetOperation: SectionOperation<T>)
//
//class SectionRecyclerTransaction<T : IRenderModelItem<*, *>>
//private constructor(
//        private val applyTransactions: List<SectionOperation<T>>,
//        private val resetTransactions: List<SectionOperation<T>>,
//        private val adapter: DataBindingRecyclerAdapter<T>,
//        private val allowExternalModifications: Boolean) {
//
//    private val isApplied = ToggleBoolean(false)
//
//    private var oldSize = 0
//
//    init {
//        oldSize = adapter.itemCount
//    }
//
//    fun apply() = isApplied.ifFalse {
//        performTransactions(applyTransactions)
//    }
//
//    fun applySafe(ifNotSafe: EmptyFunction? = null) {
//        if (canPerformTransaction()) {
//            apply()
//        } else {
//            ifNotSafe?.invoke()
//        }
//    }
//
//    fun resetSafe(ifNotSafe: EmptyFunction? = null) {
//        if (canPerformTransaction()) {
//            reset()
//        } else {
//            ifNotSafe?.invoke()
//        }
//    }
//
//    fun reset() = isApplied.ifTrue {
//        performTransactions(resetTransactions)
//    }
//
//    private fun canPerformTransaction(): Boolean =
//            adapter.itemCount == oldSize || allowExternalModifications
//
//    private fun performTransactions(opertaions: List<SectionOperation<T>>) {
//        if (!canPerformTransaction()) {
//            throw RuntimeException("External changes are not permitted on the adapter for this transaction;\r\n" +
//                    "if this is expected, set allow external modifications to true.")
//            //WARNING this is super dangerous, we are potentially unable to do our transaction.
//        }
//        opertaions.invokeEachWith(adapter)
//        oldSize = adapter.itemCount
//    }
//
//    class Builder<T : IRenderModelItem<*, *>>(private val adapter: DataBindingRecyclerAdapter<T>) {
//
//        private val operations = mutableListOf<SectionTransactionCommando<T>>()
//
//        var allowExternalModifications = false
//
//        private fun addOperation(applyOperation: SectionOperation<T>, resetOperation: SectionOperation<T>) {
//            operations.add(SectionTransactionCommando(applyOperation, resetOperation))
//        }
//
//        fun hideSection(sectionIndex: Int) {
//            val visibilityBefore = adapter.isSectionVisible(sectionIndex)
//            addOperation({ this.hideSection(sectionIndex) }, { this.setSectionVisibility(sectionIndex, visibilityBefore) })
//        }
//
//        fun showSection(sectionIndex: Int) {
//            val visibilityBefore = adapter.isSectionVisible(sectionIndex)
//            addOperation({ this.showSection(sectionIndex) }, { this.setSectionVisibility(sectionIndex, visibilityBefore) })
//        }
//
//        fun addItems(items: List<T>, inSection: Int) {
//            addOperation({ this.addAll(items, inSection) }, { this.removeAll(items, inSection) })
//        }
//
//        fun removeItems(items: List<T>, inSection: Int) {
//            addOperation({ this.removeAll(items, inSection) }, { addAll(items, inSection) })
//        }
//
//        fun addItem(item: T, inSection: Int) {
//            addOperation({ this.add(item, inSection) }, { this.remove(item, inSection) })
//        }
//
//        /**
//         * should turn invisible given the condition
//         */
//        fun hideSectionIf(condition: FunctionAdapterBoolean<T>, inSection: Int) {
//            val visibilityBefore = adapter.isSectionVisible(inSection)
//            addOperation({
//                if (condition(this)) {
//                    this.hideSection(inSection)
//                }
//            }, { this.setSectionVisibility(inSection, visibilityBefore) }) //double showing a section cannot go wrong.
//        }
//
//        /**
//         * should turn visible given the condition
//         */
//        fun showSectionIf(condition: FunctionAdapterBoolean<T>, inSection: Int) {
//            val visibilityBefore = adapter.isSectionVisible(inSection)
//            addOperation({
//                if (condition(this)) {
//                    this.showSection(inSection)
//                }
//            }, { this.setSectionVisibility(inSection, visibilityBefore) }) //double hiding a section cannot go wrong.
//        }
//
//        /**
//         *
//         */
//        fun setSectionsVisibility(sectionToShow: Int, SectionToHide: Int) {
//            addOperation({
//                this.showSection(sectionToShow)
//                this.hideSection(SectionToHide)
//            }, {
//                this.hideSection(sectionToShow)
//                this.showSection(SectionToHide)
//            })
//        }
//
//
//        fun removeItem(item: T, inSection: Int) {
//
//            adapter.getIndexFor(item, inSection)?.let {
//                this.removeItemAt(item, it.row, it.section)
//            }
//        }
//
//        fun insert(item: T, row: Int, inSection: Int) {
//            addOperation({ this.insert(item, row, inSection) }, { this.removeAt(row, inSection) })
//        }
//
//        fun removeItemAt(item: T, row: Int, inSection: Int) {
//            addOperation({ this.removeAt(row, inSection) }, {
//                if (this.getSectionSize(inSection) == null) {
//                    //section dies, recreate it.
//                    this.add(item, inSection)
//                } else {
//                    this.insert(item, row, inSection)
//                }
//            })
//        }
//
//
//        fun saveSectionsVisibilies(vararg sections: Int) {
//            val states = sections.map { Pair(it, adapter.isSectionVisible(it)) }
//            saveVisibilityForSectionsAction(states)
//        }
//
//        //TODO requies further api expansion (query the section indexes somehow)
//        /*     fun saveAllSectionsVisibilities() {
//                 val states = adapter.section .map { adapter.isSectionVisible(it) }
//                 saveVisibilityForSectionsAction(states)
//             }*/
//
//        private fun saveVisibilityForSectionsAction(sectionToVisibility: List<Pair<Int, Boolean>>) {
//            addOperation({}, {
//                sectionToVisibility.forEach {
//                    adapter.setSectionVisibility(it.first, it.second)
//                }
//            })
//        }
//
//
//        fun build(): SectionRecyclerTransaction<T> = SectionRecyclerTransaction(
//                operations.map { it.applyOperation },
//                operations.reversed().map { it.resetOperation },
//                adapter, allowExternalModifications
//        )
//    }
//
//
//}
