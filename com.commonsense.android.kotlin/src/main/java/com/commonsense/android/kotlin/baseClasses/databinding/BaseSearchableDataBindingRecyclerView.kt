package com.commonsense.android.kotlin.baseClasses.databinding

import android.content.Context
import android.databinding.ViewDataBinding
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.channels.actor
import kotlinx.coroutines.experimental.channels.consumeEach
import kotlinx.coroutines.experimental.launch

/**
 * Created by kasper on 01/06/2017.
 */

interface IRenderModelSearchItem<T : Any, Vm : ViewDataBinding, in F : Any> : IRenderModelItem<T, Vm> {
    fun isAcceptedByFilter(value: F): Boolean
}


class RenderSearchableModelItem<T : Any, Vm : ViewDataBinding, in F : Any>(val filterFunction: (F, T) -> Boolean, val renderModel: IRenderModelItem<T, Vm>) : IRenderModelItem<T, Vm> by renderModel, IRenderModelSearchItem<T, Vm, F> {
    override fun isAcceptedByFilter(value: F): Boolean = filterFunction(value, renderModel.getValue())
}

fun <T : Any, Vm : ViewDataBinding, F : Any> IRenderModelItem<T, Vm>.toSearchable(filterFunction: (F, T) -> Boolean): RenderSearchableModelItem<T, Vm, F> {
    return RenderSearchableModelItem(filterFunction, this)
}


open class BaseSearchableDataBindingRecyclerView<F : Any>(context: Context) : AbstractDataBindingRecyclerView<IRenderModelSearchItem<*, *, F>>(context) {

    private val allDataCollection = mutableListOf<IRenderModelSearchItem<*, *, F>>()
    private var filterValue: F? = null

    private val isFiltering
        get() = filterValue != null

    //the "gatekeeper" for our filter function. will restrict acces so only one gets in. thus if we spam the filter, we should only use the latest filter.
    private val eventActor = actor<Unit>(CommonPool) {
        channel.consumeEach {
            val res = allDataCollection.filter(this@BaseSearchableDataBindingRecyclerView::isAcceptedByFilter)
            updateVisibile(res)
        }
    }

    override fun add(newItem: IRenderModelSearchItem<*, *, F>) {
        allDataCollection.add(newItem)
        performActionIfIsValidFilter(newItem, { super.add(newItem) })
    }


    override fun addAll(items: List<IRenderModelSearchItem<*, *, F>>) {
        allDataCollection.addAll(items)
        items.forEach {
            performActionIfIsValidFilter(it, { super.add(it) })
        }

    }

    private inline fun performActionIfIsValidFilter(newItem: IRenderModelSearchItem<*, *, F>, crossinline action: (IRenderModelSearchItem<*, *, F>) -> Unit) {
        if (!isFiltering || isAcceptedByFilter(newItem)) {
            action(newItem)
        }
    }

    override fun remove(newItem: IRenderModelSearchItem<*, *, F>) {
        allDataCollection.remove(newItem)
        super.remove(newItem)
    }

    override fun removeAt(index: Int) {
        allDataCollection.removeAt(index)
        super.removeAt(index)
    }

    override fun clear() {
        allDataCollection.clear()
        super.clear()
    }

    private fun isAcceptedByFilter(newItem: IRenderModelSearchItem<*, *, F>): Boolean {
        return filterValue?.let(newItem::isAcceptedByFilter) ?: true

    }

    fun filterOrClearBy(potentialFilter: F?) {
        if (potentialFilter != null) {
            filterBy(potentialFilter)
        } else {
            removeFilter()
        }
    }


    fun filterBy(newFilter: F) {
        filterValue = newFilter
        eventActor.offer(Unit)
    }

    private fun updateVisibile(data: List<IRenderModelSearchItem<*, *, F>>) {
        super.clearAndSetItemsNoNotify(data)
        launch(UI) {
            super.notifyDataSetChanged()
        }
    }

    fun removeFilter() {
        filterValue = null
        updateVisibile(allDataCollection)
    }
}