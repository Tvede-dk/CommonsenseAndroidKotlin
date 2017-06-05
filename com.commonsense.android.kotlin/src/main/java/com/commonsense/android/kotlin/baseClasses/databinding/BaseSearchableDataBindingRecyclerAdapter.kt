package com.commonsense.android.kotlin.baseClasses.databinding

import android.content.Context
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import com.commonsense.android.kotlin.android.logging.L
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

interface IRenderModelSearchItem<T : Any, Vm : ViewDataBinding, in F : Any> : IRenderModelItem<T, Vm> {
    fun isAcceptedByFilter(value: F): Boolean
}


class RenderSearchableModelItem<T : Any, Vm : ViewDataBinding, in F : Any>(val filterFunction: (F, T) -> Boolean, val renderModel: IRenderModelItem<T, Vm>) : IRenderModelItem<T, Vm> by renderModel, IRenderModelSearchItem<T, Vm, F> {
    override fun isAcceptedByFilter(value: F): Boolean = filterFunction(value, renderModel.getValue())
}

abstract class BaseSearchRenderModel<T : Any, Vm : ViewDataBinding, in F : Any>(item: T, classType: Class<Vm>)
    : BaseRenderModel<T, Vm>(item, classType), IRenderModelSearchItem<T, Vm, F>


fun <T : Any, Vm : ViewDataBinding, F : Any> IRenderModelItem<T, Vm>.toSearchable(filterFunction: (F, T) -> Boolean): RenderSearchableModelItem<T, Vm, F> {
    return RenderSearchableModelItem(filterFunction, this)
}


open class BaseSearchableDataBindingRecyclerAdapter<F : Any>(context: Context) : AbstractDataBindingRecyclerAdapter<IRenderModelSearchItem<*, *, F>>(context.applicationContext) {

    private val allDataCollection = mutableListOf<IRenderModelSearchItem<*, *, F>>()
    private var filterValue: F? = null

    //the "gatekeeper" for our filter function. will restrict acces so only one gets in. thus if we spam the filter, we should only use the latest filter.
    private val filterActor: ConflatedActorHelper<F> = ConflatedActorHelper()


    suspend fun filterBySuspend(filter: F?): Unit {
        try {
            if (filter != filterValue) {
                return
            }
            val items: List<IRenderModelSearchItem<*, *, F>>
            if (filter == null) {
                L.error("test", "clearing, " + allDataCollection.count())
                items = allDataCollection.toList()
            } else {
                L.error("test", "filtering" + allDataCollection.count())
                items = allDataCollection.toList().filter { isAcceptedByFilter(it, filter) }
            }
            updateVisibly(items)
        } catch (exception: Exception) {
            L.error("fatal", "..", exception)
        }
    }

    override fun add(newItem: IRenderModelSearchItem<*, *, F>) {
        L.error("temp", "add item")
        allDataCollection.add(newItem)
        performActionIfIsValidFilter(newItem, { super.add(newItem) })
    }


    override fun addAll(items: List<IRenderModelSearchItem<*, *, F>>) {
        L.error("temp", "add All")
        allDataCollection.addAll(items)
        items.forEach {
            performActionIfIsValidFilter(it, { super.add(it) })
        }

    }

    private inline fun performActionIfIsValidFilter(newItem: IRenderModelSearchItem<*, *, F>, crossinline action: (IRenderModelSearchItem<*, *, F>) -> Unit) {
        if (isAcceptedByFilter(newItem, filterValue)) {
            action(newItem)
        }
    }

    override fun remove(newItem: IRenderModelSearchItem<*, *, F>) {
        super.remove(newItem)
        L.error("temp", "remove")
        allDataCollection.remove(newItem)
    }

    override fun removeAt(index: Int) {
        super.removeAt(index)
        L.error("temp", "removeAt")
        allDataCollection.removeAt(index)
    }

    override fun clear() {
        L.error("temp", "clear")
        allDataCollection.clear()
        super.clear()
    }

    private fun isAcceptedByFilter(newItem: IRenderModelSearchItem<*, *, F>?, value: F?): Boolean {
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
        L.error("temp", "filter by " + newFilter)
        filterValue = newFilter
//        eventActor?.offer(newFilter)
        filterActor.offer(newFilter)
    }

    private suspend fun updateVisibly(data: List<IRenderModelSearchItem<*, *, F>>) {
        L.error("temp", "update visibile")
        super.clearAndSetItemsNoNotify(data)
        launch(UI) {
            super.notifyDataSetChanged()
        }
    }

    fun removeFilter() {
        L.error("temp", "remove filter")
        filterValue = null
        filterActor.offer(null)
//        eventActor?.offer(null)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        super.onAttachedToRecyclerView(recyclerView)
        filterActor.setup {
            filterBySuspend(it)
        }
//        (eventActor == null).onTrue(this::setupActor)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView?) {
        super.onDetachedFromRecyclerView(recyclerView)
        filterActor.clear()
//        listeningRecyclers.isEmpty().onFalse { eventActor = null }
    }

}

private class ConflatedActorHelper<F : Any> {

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