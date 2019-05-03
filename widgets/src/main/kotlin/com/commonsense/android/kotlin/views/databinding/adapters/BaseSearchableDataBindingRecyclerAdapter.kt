//@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")
//
//package com.commonsense.android.kotlin.views.databinding.adapters
//
//import android.content.*
//import android.databinding.*
//import android.support.v7.widget.*
//import com.commonsense.android.kotlin.base.*
//import com.commonsense.android.kotlin.base.exceptions.*
//import com.commonsense.android.kotlin.system.datastructures.*
//import com.commonsense.android.kotlin.system.logging.*
//import kotlinx.coroutines.*
//import kotlinx.coroutines.channels.*
//
///**
// * Created by kasper on 01/06/2017.
// */
//
//interface IRenderModelSearchItem<T : Any, Vm : ViewDataBinding, in F> : IRenderModelItem<T, Vm> {
//    fun isAcceptedByFilter(value: F): Boolean
//}
//
//
//class RenderSearchableModelItem<T : Any, Vm : ViewDataBinding, in F : Any>(
//        private val filterFunction: (F, T) -> Boolean,
//        private val renderModel: IRenderModelItem<T, Vm>)
//    : IRenderModelItem<T, Vm> by renderModel, IRenderModelSearchItem<T, Vm, F> {
//    override fun isAcceptedByFilter(value: F): Boolean = filterFunction(value, renderModel.getValue())
//}
//
//abstract class BaseSearchRenderModel<T : Any, Vm : ViewDataBinding, in F : Any>(item: T, classType: Class<Vm>)
//    : BaseRenderModel<T, Vm>(item, classType), IRenderModelSearchItem<T, Vm, F>
//
//
//fun <T : Any, Vm : ViewDataBinding, F : Any> IRenderModelItem<T, Vm>.toSearchable(filterFunction: (F, T) -> Boolean):
//        RenderSearchableModelItem<T, Vm, F> = RenderSearchableModelItem(filterFunction, this)
//
//fun <T : Any, Vm : ViewDataBinding, F : Any> IRenderModelItem<T, Vm>.toSearchableIgnore(): RenderSearchableModelItem<T, Vm, F> =
//        RenderSearchableModelItem({ _: F, _: T -> false }, this)
//
//typealias IGenericSearchRender<F> = IRenderModelSearchItem<*, *, F>
//
///**
// *
// */
//open class AbstractSearchableDataBindingRecyclerAdapter<
//        T : IGenericSearchRender<F>,
//        F>(context: Context)
//    : DataBindingRecyclerAdapter<T>(context.applicationContext) {
//
//    //<editor-fold desc="Field variables">
//    private val allDataCollection: SectionLookupRep<T, InflatingFunction<*>> = SectionLookupRep()
//
//    private var filterValue: F? = null
//
//    //the "gatekeeper" for our filter function. will restrict accesses so only one gets in.
//    // thus if we spam the filter, we should only use the latest filter.
//    private val filterActor: ConflatedActorHelper<F> = ConflatedActorHelper()
//    //</editor-fold>
//
//    //<editor-fold desc="Filter suspend (background)">
//    private suspend fun filterBySuspend(filter: F?) {
//        try {
//            if (filter != filterValue) {
//                return
//            }
////            L.error("filter", "on " + allDataCollection.size)
//            val items = allDataCollection.mapAll {
//                it.filterByOurFilter()
//            }
//
////            L.error("filter", "resulted in " + items.size)
//            updateVisibly(items)
//        } catch (exception: Exception) {
//            L.error("fatal", "", exception)
//        }
//    }
//
//    private suspend fun updateVisibly(data: List<TypeSection<T>>) {
//        GlobalScope.launch(Dispatchers.Main) {
//            super.setAllSections(data)
//        }.join()
//    }
//    //</editor-fold>
//
//    //<editor-fold desc="add">
//    override fun add(newItem: T, inSection: Int) {
//        allDataCollection.add(newItem, inSection)
//        performActionIfIsValidFilter(newItem) {
//            super.add(newItem, inSection)
//        }
//    }
//
//    override fun addAll(items: Collection<T>, inSection: Int) {
//        allDataCollection.addAll(items, inSection)
//        super.addAll(items.filterByOurFilter(), inSection)
//    }
//
//
//    override fun addAll(vararg items: T, inSection: Int) {
//        val asList = items.asList()
//        allDataCollection.addAll(asList, inSection)
//        super.addAll(asList.filterByOurFilter(), inSection)
//    }
//    //</editor-fold>
//
//    //<editor-fold desc="remove">
//    override fun remove(newItem: T, inSection: Int): Int? {
//        allDataCollection.removeItem(newItem, inSection)
//        var result: Int? = null
//        performActionIfIsValidFilter(newItem) {
//            result = super.remove(newItem, inSection)
//        }
//        return result
//    }
//
//    override fun removeAt(row: Int, inSection: Int) {
//        val item = getItem(row, inSection) ?: return
//        allDataCollection.removeAt(row, inSection)
//        performActionIfIsValidFilter(item) {
//            super.removeAt(row, inSection)
//        }
//    }
//
//    override fun removeIn(range: IntRange, inSection: Int) {
//        if (filterValue != null) {
//            TODO("this is broken, should convert to list of items to remove the \"unfiltered\" once or more advancly," +
//                    " call removeAt for each (of the accepted) (in reverse order)")
//        }
//
//        allDataCollection.removeInRange(range, inSection)
//        super.removeIn(range, inSection)
//    }
//
//    override fun removeAll(items: List<T>, inSection: Int) {
//        allDataCollection.removeItems(items, inSection)
//        super.removeAll(items.filterByOurFilter(), inSection)
//    }
//    //</editor-fold>
//
//    //<editor-fold desc="insert">
//    override fun insert(item: T, atRow: Int, inSection: Int) {
//        allDataCollection.insert(item, atRow, inSection)
//        performActionIfIsValidFilter(item) {
//            super.insert(item, atRow, inSection)
//        }
//    }
//
//    override fun insertAll(items: Collection<T>, startPosition: Int, inSection: Int) {
//        allDataCollection.insertAll(items, startPosition, inSection)
//        super.insertAll(items.filterByOurFilter(), startPosition, inSection)
//    }
//
//    override fun insertAll(vararg items: T, startPosition: Int, inSection: Int) {
//        val asList = items.asList()
//        allDataCollection.insertAll(asList, startPosition, inSection)
//        super.insertAll(asList.filterByOurFilter(), startPosition, inSection)
//    }
//    //</editor-fold>
//
//    //<editor-fold desc="replace">
//    override fun replace(newItem: T, position: Int, inSection: Int) {
//        allDataCollection.replace(newItem, position, inSection)
//        performActionIfIsValidFilter(newItem) {
//            super.replace(newItem, position, inSection)
//        }
//    }
//    //</editor-fold>
//
//    //<editor-fold desc="clear">
//    override fun clear() {
//        allDataCollection.clear()
//        super.clear()
//    }
//    //</editor-fold>
//
//    //<editor-fold desc="filter">
//    private fun isAcceptedByFilter(newItem: T?, value: F?): Boolean {
//        if (value == null || newItem == null) {
//            return true
//        }
//        return newItem.isAcceptedByFilter(value)
//    }
//
//    fun filterOrClearBy(potentialFilter: F?) {
//        if (potentialFilter != null) {
//            filterBy(potentialFilter)
//        } else {
//            removeFilter()
//        }
//    }
//
//
//    fun filterBy(newFilter: F) {
//        filterValue = newFilter
//        filterActor.offer(newFilter)
//    }
//
//
//    fun removeFilter() {
//        filterValue = null
//        filterActor.offer(null)
//    }
//
//    fun getFilter(): F? = filterValue
//    //</editor-fold>
//
//    //<editor-fold desc="recycler view integration">
//    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
//        filterActor.setup { filterBySuspend(it) }
//        super.onAttachedToRecyclerView(recyclerView)
//    }
//
//    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
//        filterActor.clear()
//        super.onDetachedFromRecyclerView(recyclerView)
//    }
//    //</editor-fold>
//
//    //<editor-fold desc="Section operations">
//    override fun setSection(items: List<T>, inSection: Int) {
//        allDataCollection.setSection(items, inSection)
//        super.setSection(items.filterByOurFilter(), inSection)
//    }
//
//    /**
//     *
//     */
//    override fun removeSection(sectionIndex: Int) {
//        allDataCollection.removeSection(sectionIndex)
//        super.removeSection(sectionIndex)
//    }
//
//
//    override fun hideSection(sectionIndex: Int) {
//        allDataCollection.ignoreSection(sectionIndex)
//        super.hideSection(sectionIndex)
//    }
//
//    override fun showSection(sectionIndex: Int) {
//        allDataCollection.acceptSection(sectionIndex)
//        super.showSection(sectionIndex)
//    }
//    //</editor-fold>
//
//    //<editor-fold desc="Helper functions">
//    private inline fun performActionIfIsValidFilter(newItem: T, crossinline action: FunctionUnit<T>) {
//        if (isAcceptedByFilter(newItem, filterValue)) {
//            action(newItem)
//        }
//    }
//
//    private fun Collection<T>.filterByOurFilter(): List<T> =
//            filter { isAcceptedByFilter(it, filterValue) }
//    //</editor-fold>
//}
//
///**
// * Creates a single edition of the conflator (so multiple calls to setup results in only 1 been made).
// */
//private class ConflatedActorHelper<F> {
//
//    private var eventActor: SendChannel<F?>? = null
//
//    fun offer(filter: F?) {
//        val actor = eventActor
//        if (actor != null) {
//            actor.offer(filter)
//        } else {
//            L.error(ConflatedActorHelper::class.java.simpleName, "the actor is null ," +
//                    "thus the recycler adapter does not have a view attached to it." +
//                    "A view is needed to filter", StackTraceException())
//        }
//    }
//
//    fun setup(callback: suspend (F?) -> Unit) {
//        if (eventActor != null) {
//            return
//        }
//
//        eventActor = GlobalScope.actor(Dispatchers.Default, capacity = Channel.CONFLATED) {
//            channel.consumeEach { callback(it) }
//        }
//    }
//
//    fun clear() {
//        eventActor = null
//    }
//}
//
//
//open class BaseSearchableDataBindingRecyclerAdapter<Filter>(context: Context)
//    : AbstractSearchableDataBindingRecyclerAdapter<
//        IRenderModelSearchItem<*, *, Filter>,
//        Filter>(context)
//
//
//open class DefaultSearchableDatabindingRecyclerAdapter<
//        T : IGenericSearchRender<F>,
//        F>(context: Context)
//    : AbstractSearchableDataBindingRecyclerAdapter<T, F>(context)