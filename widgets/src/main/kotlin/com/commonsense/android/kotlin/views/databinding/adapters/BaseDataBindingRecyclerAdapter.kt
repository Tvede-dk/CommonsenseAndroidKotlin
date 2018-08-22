package com.commonsense.android.kotlin.views.databinding.adapters

import android.content.Context
import android.databinding.ViewDataBinding
import android.support.annotation.AnyThread
import android.support.annotation.IntRange
import android.support.annotation.UiThread
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.commonsense.android.kotlin.base.extensions.cast
import com.commonsense.android.kotlin.base.extensions.collections.ifTrue
import com.commonsense.android.kotlin.base.extensions.collections.length
import com.commonsense.android.kotlin.base.extensions.isNullOrEqualTo
import com.commonsense.android.kotlin.base.extensions.use
import com.commonsense.android.kotlin.system.datastructures.IndexPath
import com.commonsense.android.kotlin.system.datastructures.SectionLookupRep
import com.commonsense.android.kotlin.system.datastructures.TypeHashCodeLookupRepresent
import com.commonsense.android.kotlin.system.datastructures.TypeSection
import com.commonsense.android.kotlin.system.logging.L
import com.commonsense.android.kotlin.views.ViewInflatingFunction
import java.lang.ref.WeakReference
import kotlin.reflect.KClass
import kotlin.system.measureNanoTime

/**
 * Created by kasper on 17/05/2017.
 */

/**
 *
 */
typealias InflatingFunction<Vm> = (inflater: LayoutInflater, parent: ViewGroup?, attach: Boolean) -> BaseViewHolderItem<Vm>


/**
 * Defines the required information for a data binding recycler adapter.
 */
open class BaseViewHolderItem<out T : ViewDataBinding>(val item: T) : RecyclerView.ViewHolder(item.root) {
    /**
     * The view's "type", which is the type of the class (which is unique, by jvm specification).
     */
    val viewBindingTypeValue = item.javaClass.hashCode()
}

/**
 *
 */
interface IRenderModelItem<T : Any, Vm : ViewDataBinding> :
        TypeHashCodeLookupRepresent<InflatingFunction<Vm>> {

    fun getValue(): T

    fun renderFunction(view: Vm, model: T, viewHolder: BaseViewHolderItem<Vm>)

    fun bindToViewHolder(holder: BaseViewHolderItem<*>)

    fun createViewHolder(inflatedView: Vm): BaseViewHolderItem<Vm>

    fun getInflaterFunction(): ViewInflatingFunction<Vm>
}

/**
 *  The Root of databinding render models (factors the most common stuff out)
 *  creates a renderable model that can render it self.
 */
abstract class BaseRenderModel<
        T : Any,
        Vm : ViewDataBinding>(val item: T, classType: Class<Vm>)
    : IRenderModelItem<T, Vm> {

    /**
     * Convenience constructor, same as original but using kotlin's classes instead.
     */
    constructor(item: T, classType: KClass<Vm>) : this(item, classType.java)


    override fun getValue(): T = item

    override fun getTypeValue() = vmTypeValue
    private val vmTypeValue: Int by lazy {
        classType.hashCode()
    }

    override fun bindToViewHolder(holder: BaseViewHolderItem<*>) {
        val casted = holder.cast<BaseViewHolderItem<Vm>>()
        if (casted != null) {
            renderFunction(casted.item, item, casted)
            //we are now "sure" that the binding class is the same as ours, thus casting "should" be "ok". (we basically introduced our own type system)
        } else {
            L.debug("RenderModelItem",
                    "unable to bind to view even though it should be correct type$vmTypeValue expected, got : ${holder.viewBindingTypeValue}")
        }
    }

    override fun createViewHolder(inflatedView: Vm): BaseViewHolderItem<Vm> =
            BaseViewHolderItem(inflatedView)

    override fun getCreatorFunction(): InflatingFunction<Vm> {
        return { inflater: LayoutInflater, parent: ViewGroup?, attach: Boolean ->
            createViewHolder(getInflaterFunction().invoke(inflater, parent, attach))
        }
    }
}

/**
 * A simple renderable model containing all information required for a databinding recycler adapter
 */
open class RenderModel<
        T : Any,
        Vm : ViewDataBinding>(private val item: T,
                              private val vmInflater: ViewInflatingFunction<Vm>,
                              private val classType: Class<Vm>,
                              private val vmRender: (view: Vm, model: T, viewHolder: BaseViewHolderItem<Vm>) -> Unit)
    : IRenderModelItem<T, Vm> {
    override fun getInflaterFunction() = vmInflater


    override fun createViewHolder(inflatedView: Vm): BaseViewHolderItem<Vm> =
            BaseViewHolderItem(inflatedView)

    override fun getCreatorFunction(): InflatingFunction<Vm> {
        return { inflater: LayoutInflater, parent: ViewGroup?, attach: Boolean ->
            createViewHolder(vmInflater(inflater, parent, attach))
        }
    }

    override fun getValue(): T = item

    override fun getTypeValue(): Int = vmTypeValue

    override fun renderFunction(view: Vm, model: T, viewHolder: BaseViewHolderItem<Vm>) = vmRender(view, model, viewHolder)

    override fun bindToViewHolder(holder: BaseViewHolderItem<*>) {
        val casted = holder.cast<BaseViewHolderItem<Vm>>()
        if (casted != null) {
            @Suppress("UNCHECKED_CAST")
            renderFunction(casted.item, item, casted)
            //we are now "sure" that the binding class is the same as ours, thus casting "should" be "ok". (we basically introduced our own type system)
        } else {
            L.debug("RenderModelItem", "unable to bind to view even though it should be correct type$vmTypeValue expected, got : ${holder.viewBindingTypeValue}")
        }
    }

    //more performance than an inline getter that retrieves it.
    private val vmTypeValue: Int by lazy {
        classType.hashCode()
    }
}

/**
 *Base class for data binding recycler adapters.
 */
abstract class DataBindingRecyclerAdapter<T>(context: Context) :
        RecyclerView.Adapter<BaseViewHolderItem<*>>() where T : IRenderModelItem<*, *> {

    override fun getItemId(position: Int): Long = RecyclerView.NO_ID

    private val dataCollection: SectionLookupRep<T, InflatingFunction<*>> = SectionLookupRep()

    private val listeningRecyclers = mutableSetOf<WeakReference<RecyclerView>>()

    private val inflater: LayoutInflater by lazy {
        LayoutInflater.from(context)
    }

    val sectionCount: Int
        get() = dataCollection.sectionCount


    override fun onCreateViewHolder(parent: ViewGroup, @IntRange(from = 0) viewType: Int): BaseViewHolderItem<*> {
        val rep = dataCollection.getTypeRepresentativeFromTypeValue(viewType)
        return rep?.invoke(inflater, parent, false)
                ?: throw RuntimeException("could not find item, even though we expected it, for viewtype: $viewType")
    }

    override fun getItemViewType(@IntRange(from = 0) position: Int): Int {
        val index = dataCollection.indexToPath(position) ?: return 0
        return dataCollection[index]?.getTypeValue() ?: 0
    }

    override fun onBindViewHolder(holder: BaseViewHolderItem<*>, @IntRange(from = 0) position: Int) {
        //lookup type to converter, then apply model on view using converter
        val index = dataCollection.indexToPath(position) ?: return
        val render = dataCollection[index]
        render?.bindToViewHolder(holder)
    }


    override fun getItemCount(): Int = dataCollection.size

    open fun add(newItem: T, inSection: Int): Unit = updateData {
        dataCollection.add(newItem, inSection)?.rawRow?.apply {
            notifyItemInserted(this)
        }
    }

    open fun addAll(items: Collection<T>, inSection: Int): Unit = updateData {
        dataCollection.addAll(items, inSection)?.inRaw?.apply {
            notifyItemRangeInserted(this.start, this.length)
        }

    }

    open fun insert(item: T, atRow: Int, inSection: Int): Unit = updateData {
        dataCollection.insert(item, atRow, inSection)?.rawRow?.apply {
            notifyItemInserted(this)
        }
    }

    open fun insertAll(items: Collection<T>, startPosition: Int, inSection: Int): Unit = updateData {
        dataCollection.insertAll(items, inSection, startPosition)?.inRaw?.apply {
            notifyItemRangeInserted(start, length)
        }
    }

    open fun addAll(vararg items: T, inSection: Int): Unit = updateData {
        dataCollection.addAll(items.asList(), inSection)?.inRaw?.apply {
            notifyItemRangeInserted(start, length)
        }
    }

    open fun insertAll(vararg items: T, startPosition: Int, inSection: Int): Unit = updateData {
        dataCollection.insertAll(items.asList(), startPosition, inSection)?.inRaw?.apply {
            notifyItemRangeInserted(start, length)
        }
    }

    open fun remove(newItem: T, inSection: Int): Int? = updateData {
        return@updateData dataCollection.removeItem(newItem, inSection)?.apply {
            notifyItemRemoved(rawRow)
        }?.rawRow
    }

    open fun removeAt(row: Int, inSection: Int): Unit = updateData {
        dataCollection.removeAt(row, inSection)?.rawRow?.apply {
            notifyItemRemoved(this)
        }
    }

    open fun removeAll(items: List<T>, inSection: Int): Unit = updateData {
        items.forEach {
            remove(it, inSection)
        }
    }


    open fun removeIn(range: kotlin.ranges.IntRange, inSection: Int): Unit = updateData {
        dataCollection.removeInRange(range, inSection)?.inRaw?.apply {
            notifyItemRangeRemoved(start + range.start, range.length)
        }
    }


    open fun getItem(atRow: Int, inSection: Int): T? = dataCollection[atRow, inSection]

    open fun clear(): Unit = updateData {
        dataCollection.clear()
        notifyDataSetChanged()
    }


    protected fun addNoNotify(item: T, inSection: Int): Unit = updateData {
        dataCollection.add(item, inSection)?.rawRow
    }

    protected fun addNoNotify(items: List<T>, inSection: Int): Unit = updateData {
        dataCollection.addAll(items, inSection)?.inRaw
    }

    open fun setSection(items: List<T>, inSection: Int) = updateData {
        val (changes, added, removed) = dataCollection.setSection(items, inSection)
                ?: return@updateData
        changes?.let {
            notifyItemRangeChanged(it.inRaw.start, it.inRaw.length)
        }
        added?.let {
            notifyItemRangeInserted(it.inRaw.start, it.inRaw.length)
        }
        removed?.let {
            notifyItemRangeRemoved(it.inRaw.start, it.inRaw.length)
        }
    }

    fun setSection(item: T, inSection: Int) = setSection(listOf(item), inSection)

    @UiThread
    fun clearSection(inSection: Int): Unit = updateData {
        dataCollection.clearSection(inSection)?.inRaw.apply {
            this?.let { notifyItemRangeRemoved(it.start, it.length) }
        }
    }

    open fun replace(newItem: T, position: Int, inSection: Int): Unit = updateData {
        dataCollection.replace(newItem, position, inSection)?.rawRow.apply {
            this?.let { notifyItemChanged(it) }
        }
    }

    @UiThread
    protected fun clearAndSetItemsNoNotify(items: List<T>, inSection: Int, isIgnored: Boolean) = updateData {
        dataCollection.setSection(items, inSection)
        isIgnored.ifTrue { dataCollection.ignoreSection(inSection) }
    }

    @UiThread
    protected fun setAllSections(sections: List<TypeSection<T>>) = updateData {
        dataCollection.setAllSections(sections)
        super.notifyDataSetChanged()
    }

    private fun stopScroll() {
        listeningRecyclers.forEach { recyclerView ->
            recyclerView.get()?.stopScroll()
        }
    }


    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        listeningRecyclers.add(WeakReference(recyclerView))
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        listeningRecyclers.removeAll {
            it.get().isNullOrEqualTo(recyclerView)
        }
    }


    /**
     * must be called from all things that manipulate the dataCollection.
     */
    private inline fun <T> updateData(crossinline action: () -> T): T {
        stopScroll()
        return action()
    }

    open fun getRepresentUsingType(viewHolderItem: BaseViewHolderItem<*>): InflatingFunction<*>? =
            dataCollection.getTypeRepresentativeFromTypeValue(viewHolderItem.viewBindingTypeValue)

    open fun getItemFromRawIndex(rawIndex: Int): T? {
        val index = dataCollection.indexToPath(rawIndex) ?: return null
        return dataCollection[index]
    }

    open fun hideSection(sectionIndex: Int) = updateData {
        val sectionLocation = dataCollection.ignoreSection(sectionIndex)?.inRaw ?: return@updateData
        notifyItemRangeRemoved(sectionLocation.start, sectionLocation.length)
    }

    open fun showSection(sectionIndex: Int) = updateData {
        val sectionLocation = dataCollection.acceptSection(sectionIndex)?.inRaw ?: return@updateData
        notifyItemRangeInserted(sectionLocation.start, sectionLocation.length)
    }

    open fun toggleSectionsVisibility(vararg sectionIndexes: Int) {
        sectionIndexes.forEach(this::toggleSectionVisibility)
    }

    open fun toggleSectionVisibility(sectionIndex: Int) {
        val sectionData = dataCollection.sectionAt(sectionIndex) ?: return
        if (sectionData.isIgnored) {
            showSection(sectionIndex)
        } else {
            hideSection(sectionIndex)
        }
    }

    @AnyThread
    protected fun clearNoNotify() {
        dataCollection.clear()
    }

    fun getIndexFor(item: T, @IntRange(from = 0) inSection: Int): IndexPath? {
        val innerIndex = dataCollection.sectionAt(inSection)?.collection?.indexOf(item)
                ?: return null
        return IndexPath(innerIndex, inSection)
    }

    fun getSectionSize(sectionIndex: Int): Int? = dataCollection.sectionAt(sectionIndex)?.size

    fun isSectionVisible(inSection: Int): Boolean =
            dataCollection.sectionAt(inSection)?.isIgnored?.not() ?: false

    fun setSectionVisibility(section: Int, isVisible: Boolean) {
        dataCollection.sectionAt(section)?.let {
            if (it.isIgnored != !isVisible) {
                toggleSectionVisibility(section)
            }
        }
    }

    /**
     * Removes the section by index, iff it exists.
     */
    open fun removeSection(@IntRange(from = 0) sectionIndex: Int) = clearSection(sectionIndex)

    fun removeSections(@IntRange(from = 0) vararg sectionIndexes: Int) {
        sectionIndexes.forEach(this::removeSection)
    }


    fun smoothScrollToSection(@IntRange(from = 0) sectionIndex: Int) {
        val positionInList = dataCollection.getSectionLocation(sectionIndex)?.inRaw?.first ?: return
        listeningRecyclers.forEach {
            it.use { this.smoothScrollToPosition(positionInList) }
        }
    }

}

/**
 *
 * @receiver BaseDataBindingRecyclerAdapter
 * @param sections IntArray
 */
fun BaseDataBindingRecyclerAdapter.hideSections(vararg sections: Int) =
        sections.forEach(this::hideSection)

/**
 *
 * @receiver BaseDataBindingRecyclerAdapter
 * @param sections IntArray
 */
fun BaseDataBindingRecyclerAdapter.showSections(vararg sections: Int) =
        sections.forEach(this::showSection)

/**  */
open class BaseDataBindingRecyclerAdapter(context: Context) :
        DataBindingRecyclerAdapter<IRenderModelItem<*, *>>(context)


class DefaultDataBindingRecyclerAdapter(context: Context) : BaseDataBindingRecyclerAdapter(context)