package com.commonsense.android.kotlin.views.databinding.adapters

import android.content.Context
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import com.commonsense.android.kotlin.system.datastructures.TypeSection
import com.commonsense.android.kotlin.system.datastructures.TypeSectionLookupRepresentative
import com.commonsense.android.kotlin.system.logging.L
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

//TODO performActionIfIsValidFilter for all modifiers ??????
/**
 *
 */
open class AbstractSearchableDataBindingRecyclerAdapter<
        T : IGenericSearchRender<F>,
        F>(context: Context)
    : DataBindingRecyclerAdapter<T>(context.applicationContext) {

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
            L.error("fatal", "", exception)
        }
    }

    override fun add(newItem: T, inSection: Int) {
        allDataCollection.add(newItem, inSection)
        performActionIfIsValidFilter(newItem, { super.add(newItem, inSection) })
    }

    override fun addAll(items: Collection<T>, inSection: Int) {
        allDataCollection.addAll(items, inSection)
        items.forEach {
            performActionIfIsValidFilter(it, { super.add(it, inSection) })
        }
    }

    private inline fun performActionIfIsValidFilter(newItem: T, crossinline action: (T) -> Unit) {
        if (isAcceptedByFilter(newItem, filterValue)) {
            action(newItem)
        }
    }

    override fun remove(newItem: T, inSection: Int): Int? {
        allDataCollection.removeItem(newItem, inSection)
        return super.remove(newItem, inSection)
    }

    override fun removeAt(row: Int, inSection: Int) {
        allDataCollection.removeAt(row, inSection)
        super.removeAt(row, inSection)
    }

    override fun insertAll(items: Collection<T>, startPosition: Int, inSection: Int) {
        allDataCollection.insertAll(items, startPosition, inSection)
        super.insertAll(items, startPosition, inSection)
    }

    override fun insertAll(vararg items: T, startPosition: Int, inSection: Int) {
        val asList = items.asList()
        allDataCollection.insertAll(asList, startPosition, inSection)
        super.insertAll(asList, startPosition, inSection)
    }

    override fun addAll(vararg items: T, inSection: Int) {
        val asList = items.asList()
        allDataCollection.addAll(asList, inSection)
        super.addAll(asList, inSection)
    }

    override fun replace(newItem: T, position: Int, inSection: Int) {
        allDataCollection.replace(newItem, position, inSection)
        super.replace(newItem, position, inSection)
    }

    override fun removeIn(range: IntRange, inSection: Int) {
        allDataCollection.removeInRange(range, inSection)
        super.removeIn(range, inSection)
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
        filterActor.setup {
            filterBySuspend(it)
        }
        super.onAttachedToRecyclerView(recyclerView)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView?) {
        filterActor.clear()
        super.onDetachedFromRecyclerView(recyclerView)
    }

    fun getFilter(): F? = filterValue

    override fun insert(item: T, atRow: Int, inSection: Int) {
        allDataCollection.insert(item, atRow, inSection)
        super.insert(item, atRow, inSection)
    }

    override fun setSection(items: List<T>, inSection: Int) {
        allDataCollection.setSection(items, inSection)
        super.setSection(items, inSection)
    }


    override fun hideSection(sectionIndex: Int) {
        allDataCollection.ignoreSection(sectionIndex)
        super.hideSection(sectionIndex)
    }

    override fun showSection(sectionIndex: Int) {
        allDataCollection.acceptSection(sectionIndex)
        super.showSection(sectionIndex)
    }

    override fun removeAll(items: List<T>, inSection: Int) {
        allDataCollection.removeItems(items, inSection)
        super.removeAll(items, inSection)
    }
}

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


open class BaseSearchableDataBindingRecyclerAdapter<Filter>(context: Context)
    : AbstractSearchableDataBindingRecyclerAdapter<
        IRenderModelSearchItem<*, *, Filter>,
        Filter>(context)


open class DefaultSearchableDatabindingRecyclerAdapter<
        T : IGenericSearchRender<F>,
        F>(context: Context)
    : AbstractSearchableDataBindingRecyclerAdapter<T, F>(context)