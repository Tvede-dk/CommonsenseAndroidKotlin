package com.commonsense.android.kotlin.baseClasses.databinding

import android.content.Context
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import com.commonsense.android.kotlin.android.logging.L
import com.commonsense.android.kotlin.extensions.collections.replace
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.channels.ActorJob
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.actor
import kotlinx.coroutines.experimental.channels.consumeEach
import kotlinx.coroutines.experimental.launch

/**
 * Created by kasper on 01/06/2017.
 */

interface IRenderModelSearchItem<T : Any, Vm : ViewDataBinding, in F> : IRenderModelItem<T, Vm> {
    fun isAcceptedByFilter(value: F): Boolean
}


class RenderSearchableModelItem<T : Any, Vm : ViewDataBinding, in F : Any>(
        val filterFunction: (F, T) -> Boolean,
        val renderModel: IRenderModelItem<T, Vm>)
    : IRenderModelItem<T, Vm> by renderModel, IRenderModelSearchItem<T, Vm, F> {
    override fun isAcceptedByFilter(value: F): Boolean = filterFunction(value, renderModel.getValue())
}

abstract class BaseSearchRenderModel<T : Any, Vm : ViewDataBinding, in F : Any>(item: T, classType: Class<Vm>)
    : BaseRenderModel<T, Vm>(item, classType), IRenderModelSearchItem<T, Vm, F>


fun <T : Any, Vm : ViewDataBinding, F : Any> IRenderModelItem<T, Vm>.toSearchable(filterFunction: (F, T) -> Boolean):
        RenderSearchableModelItem<T, Vm, F> {
    return RenderSearchableModelItem(filterFunction, this)
}

fun <T : Any, Vm : ViewDataBinding, F : Any> IRenderModelItem<T, Vm>.toSearchableIgnore(): RenderSearchableModelItem<T, Vm, F> {
    return RenderSearchableModelItem({ _: F, _: T -> false }, this)
}

typealias IGenericSearchRender<F> = IRenderModelSearchItem<*, *, F>

open class AbstractSearchableDataBindingRecyclerAdapter<
        T : IGenericSearchRender<F>,
        F>(context: Context)
    : AbstractDataBindingRecyclerAdapter<T>(context.applicationContext) {

    private val allDataCollection = mutableListOf<T>()
    private var filterValue: F? = null

    //the "gatekeeper" for our filter function. will restrict acces so only one gets in. thus if we spam the filter, we should only use the latest filter.
    private val filterActor: ConflatedActorHelper<F> = ConflatedActorHelper()


    private suspend fun filterBySuspend(filter: F?): Unit {
        try {
            if (filter != filterValue) {
                return
            }
            val items: List<IRenderModelSearchItem<*, *, F>>
            if (filter == null) {
                items = allDataCollection.toList()
            } else {
                items = allDataCollection.toList().filter { isAcceptedByFilter(it, filter) }
            }
            updateVisibly(items)
        } catch (exception: Exception) {
            L.error("fatal", "..", exception)
        }
    }

    override fun add(newItem: T, atSection: Int) {
        allDataCollection.add(newItem)
        performActionIfIsValidFilter(newItem, { super.add(newItem, atSection) })
    }

    override fun addAll(items: Collection<T>, atSection: Int) {
        allDataCollection.addAll(items)
        items.forEach {
            performActionIfIsValidFilter(it, { super.add(it, atSection) })
        }

    }

    private inline fun performActionIfIsValidFilter(newItem: T, crossinline action: (T) -> Unit) {
        if (isAcceptedByFilter(newItem, filterValue)) {
            action(newItem)
        }
    }

    override fun remove(newItem: T, atSection: Int) {
        super.remove(newItem, atSection)
        allDataCollection.remove(newItem)
    }

    override fun removeAt(row: Int, inSection: Int) {
        super.removeAt(row, inSection)
        allDataCollection.removeAt(row)
    }

    override fun addAll(items: Collection<T>, atSection: Int, startPosition: Int) {
        super.addAll(items, startPosition)
        allDataCollection.addAll(startPosition, items)
    }

    override fun addAll(vararg items: T, atSection: Int, startPosition: Int) {
        val asList = items.asList()
        super.addAll(asList, startPosition)
        allDataCollection.addAll(startPosition, asList)
    }

    override fun replace(newItem: T, position: Int) {
        super.replace(newItem, position)
        allDataCollection.replace(newItem, position)
    }

    override fun removeIn(range: IntRange, atSection: Int) {
        super.removeIn(range, atSection)
        dataCollection.removeInRange(range, atSection)
    }


    override fun clear() {
        allDataCollection.clear()
        super.clear()
    }

    private fun isAcceptedByFilter(newItem: T?, value: F?): Boolean {
        if (value == null || newItem == null) {
            return true
        }
        return newItem.isAcceptedByFilter(value)
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
        filterActor.offer(newFilter)
    }

    private suspend fun updateVisibly(data: List<T>) {
        super.clearAndSetItemsNoNotify(data, 0)//TODO !!!!!!!!!!!!
        launch(UI) {
            super.notifyDataSetChanged()
        }
    }

    fun removeFilter() {
        filterValue = null
        filterActor.offer(null)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        super.onAttachedToRecyclerView(recyclerView)
        filterActor.setup {
            filterBySuspend(it)
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView?) {
        super.onDetachedFromRecyclerView(recyclerView)
        filterActor.clear()
    }

    fun getFilter(): F? {
        return filterValue
    }
}

open class BaseSearchableDataBindingRecyclerAdapter<Filter>(context: Context)
    : AbstractSearchableDataBindingRecyclerAdapter<
        IRenderModelSearchItem<*, *, Filter>,
        Filter>(context)


private class ConflatedActorHelper<F> {

    private var eventActor: ActorJob<F?>? = null

    fun offer(filter: F?) {

        eventActor?.offer(filter)
    }

    fun setup(callback: suspend (F?) -> Unit) {
        if (eventActor != null) {
            return
        }
        eventActor = actor(CommonPool, capacity = Channel.CONFLATED) {
            channel.consumeEach(callback)
        }
    }

    fun clear() {
        eventActor = null
    }

}

open class DefaultSearchableDatabindingRecyclerAdapter<
        T : IGenericSearchRender<F>,
        F>(context: Context)
    : AbstractSearchableDataBindingRecyclerAdapter<T, F>(context)