package com.commonsense.android.kotlin.baseClasses.databinding

import android.content.Context
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import com.commonsense.android.kotlin.android.logging.L
import com.commonsense.android.kotlin.collections.TypeSection
import com.commonsense.android.kotlin.collections.TypeSectionLookupRepresentative
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
        private val filterFunction: (F, T) -> Boolean,
        private val renderModel: IRenderModelItem<T, Vm>)
    : IRenderModelItem<T, Vm> by renderModel, IRenderModelSearchItem<T, Vm, F> {
    override fun isAcceptedByFilter(value: F): Boolean = filterFunction(value, renderModel.getValue())
}

abstract class BaseSearchRenderModel<T : Any, Vm : ViewDataBinding, in F : Any>(item: T, classType: Class<Vm>)
    : BaseRenderModel<T, Vm>(item, classType), IRenderModelSearchItem<T, Vm, F>


fun <T : Any, Vm : ViewDataBinding, F : Any> IRenderModelItem<T, Vm>.toSearchable(filterFunction: (F, T) -> Boolean):
        RenderSearchableModelItem<T, Vm, F> = RenderSearchableModelItem(filterFunction, this)

fun <T : Any, Vm : ViewDataBinding, F : Any> IRenderModelItem<T, Vm>.toSearchableIgnore(): RenderSearchableModelItem<T, Vm, F> =
        RenderSearchableModelItem({ _: F, _: T -> false }, this)

typealias IGenericSearchRender<F> = IRenderModelSearchItem<*, *, F>

open class AbstractSearchableDataBindingRecyclerAdapter<
        T : IGenericSearchRender<F>,
        F>(context: Context)
    : AbstractDataBindingRecyclerAdapter<T>(context.applicationContext) {

    private val allDataCollection: TypeSectionLookupRepresentative<T, InflatingFunction<*>>
            = TypeSectionLookupRepresentative()

    private var filterValue: F? = null

    //the "gatekeeper" for our filter function. will restrict acces so only one gets in. thus if we spam the filter, we should only use the latest filter.
    private val filterActor: ConflatedActorHelper<F> = ConflatedActorHelper()


    private suspend fun filterBySuspend(filter: F?) {
        try {
            if (filter != filterValue) {
                return
            }

            L.error("filter", "on " + allDataCollection.size)
            val items = allDataCollection.mapAll {
                it.filter { isAcceptedByFilter(it, filter) }
            }

            L.error("filter", "resulted in " + items.size)
            updateVisibly(items)
        } catch (exception: Exception) {
            L.error("fatal", "..", exception)
        }
    }

    override fun add(newItem: T, atSection: Int) {
        allDataCollection.add(newItem, atSection)
        performActionIfIsValidFilter(newItem, { super.add(newItem, atSection) })
    }

    override fun addAll(items: Collection<T>, atSection: Int) {
        allDataCollection.addAll(items, atSection)
        items.forEach {
            performActionIfIsValidFilter(it, { super.add(it, atSection) })
        }

    }

    private inline fun performActionIfIsValidFilter(newItem: T, crossinline action: (T) -> Unit) {
        if (isAcceptedByFilter(newItem, filterValue)) {
            action(newItem)
        }
    }

    override fun remove(newItem: T, atSection: Int): Int {
        val index = super.remove(newItem, atSection)
        allDataCollection.removeItem(newItem, atSection)
        return index
    }

    override fun removeAt(row: Int, inSection: Int) {
        super.removeAt(row, inSection)
        allDataCollection.removeAt(row, inSection)
    }

    override fun addAll(items: Collection<T>, startPosition: Int, atSection: Int) {
        super.addAll(items, startPosition)
        allDataCollection.addAll(items, atSection, startPosition)
    }

    override fun addAll(vararg items: T, startPosition: Int, atSection: Int) {
        val asList = items.asList()
        super.addAll(asList, startPosition)
        allDataCollection.addAll(asList, atSection, startPosition)
    }

    override fun addAll(vararg items: T, atSection: Int) {
        val asList = items.asList()
        super.addAll(asList, atSection)
        allDataCollection.addAll(asList, atSection)
    }

    override fun replace(newItem: T, position: Int, atSection: Int) {
        super.replace(newItem, position, atSection)
        allDataCollection.replace(newItem, position, atSection)
    }

    override fun removeIn(range: IntRange, atSection: Int) {
        super.removeIn(range, atSection)
        allDataCollection.removeInRange(range, atSection)
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

    private suspend fun updateVisibly(data: List<TypeSection<T>>) {
        launch(UI) {
            super.setAllSections(data)

        }.join()
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

    fun getFilter(): F? = filterValue

    override fun add(item: T, atRow: Int, atSection: Int) {
        super.add(item, atRow, atSection)
        allDataCollection.add(item, atRow, atSection)
    }

    override fun setSection(items: List<T>, atSection: Int) {
        allDataCollection.clearAndSetSection(items, atSection)
        super.setSection(items, atSection)
    }


    override fun hideSection(sectionIndex: Int) {
        allDataCollection.ignoreSection(sectionIndex)
        super.hideSection(sectionIndex)
    }

    override fun showSection(sectionIndex: Int) {
        allDataCollection.acceptSection(sectionIndex)
        super.showSection(sectionIndex)
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