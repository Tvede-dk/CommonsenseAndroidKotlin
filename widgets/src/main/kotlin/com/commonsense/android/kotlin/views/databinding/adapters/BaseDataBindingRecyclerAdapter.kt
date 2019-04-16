@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.views.databinding.adapters

import android.content.*
import android.databinding.*
import android.support.annotation.*
import android.support.annotation.IntRange
import android.support.v7.widget.*
import android.view.*
import com.commonsense.android.kotlin.base.*
import com.commonsense.android.kotlin.base.extensions.*
import com.commonsense.android.kotlin.base.extensions.collections.*
import com.commonsense.android.kotlin.system.datastructures.*
import com.commonsense.android.kotlin.system.logging.*
import com.commonsense.android.kotlin.views.*
import com.commonsense.android.kotlin.views.extensions.*
import java.lang.ref.*
import kotlin.reflect.*

/**
 * Created by kasper on 17/05/2017.
 */

/**
 *  Describes the required for inflating a given ViewVBinding
 *  VM is the viewbinding class
 */
typealias InflatingFunction<Vm> = (inflater: LayoutInflater, parent: ViewGroup?, attach: Boolean) -> BaseViewHolderItem<Vm>


/**
 * Defines the required information for a data binding recycler adapter.
 * @param T : ViewDataBinding the view to contain
 * @property item T the data for the view
 * @property viewBindingTypeValue Int the view type (a unique number for the view)
 */
open class BaseViewHolderItem<out T : ViewDataBinding>(val item: T) : RecyclerView.ViewHolder(item.root) {
    /**
     * The view's "type", which is the type of the class (which is unique, by jvm specification).
     */
    val viewBindingTypeValue = item.javaClass.hashCode()
}

/**
 *
 * @param T : Any
 * @param Vm : ViewDataBinding
 */
interface IRenderModelItem<T : Any, Vm : ViewDataBinding> :
        TypeHashCodeLookupRepresent<InflatingFunction<Vm>> {

    /**
     * Gets the data associated with this binding
     * @return T
     */
    fun getValue(): T

    /**
     * Renders the given model to the given view, via the view holder
     * @param view Vm the view to update with the model
     * @param model T the model data associated with this view type
     * @param viewHolder BaseViewHolderItem<Vm> the view holder containing the view and data
     */
    fun renderFunction(view: Vm, model: T, viewHolder: BaseViewHolderItem<Vm>)

    /**
     * Binds this to the given view holder.
     * @param holder BaseViewHolderItem<*>
     */
    fun bindToViewHolder(holder: BaseViewHolderItem<*>)

    /**
     * Creates the view holder from the given (potentially newly inflated) view.
     * @param inflatedView Vm the view binding
     * @return BaseViewHolderItem<Vm> a valid view holder for the given view binding
     */
    fun createViewHolder(inflatedView: Vm): BaseViewHolderItem<Vm>

    /**
     * Gets the inflater function to create the view for this
     * @return ViewInflatingFunction<Vm> the function that can create the view;
     */
    fun getInflaterFunction(): ViewInflatingFunction<Vm>
}

/**
 *  The Root of databinding render models (factors the most common stuff out)
 *  creates a renderable model that can render it self.

 * @param T : Any the data associated with this render
 * @param Vm : ViewDataBinding the view associated with this render
 */
abstract class BaseRenderModel<
        T : Any,
        Vm : ViewDataBinding>(val item: T, classType: Class<Vm>)
    : IRenderModelItem<T, Vm> {

    /**
     * Convenience constructor, same as original but using kotlin's classes instead.
     * @param item T the data to use
     * @param classType KClass<Vm> the view class type
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
 * @param T : Any the data associated with this render
 * @param Vm : ViewDataBinding the view associated with this render
 * @constructor
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

    /**
     * more performance than an inline getter that retrieves it.
     */
    private val vmTypeValue: Int by lazy {
        classType.hashCode()
    }
}


/**
 *  Base class for data binding recycler adapters.
 * @param T the type of render models
 */
abstract class DataBindingRecyclerAdapter<T>(context: Context) :
        RecyclerView.Adapter<BaseViewHolderItem<*>>() where T : IRenderModelItem<*, *> {

    /**
     * A simple implementation that discards / tells the underlying adapter that each item have no id.
     * @param position Int
     * @return Long RecyclerView.NO_ID
     */
    override fun getItemId(position: Int): Long = RecyclerView.NO_ID

    /**
     * The container for all the data via sections
     */
    private val dataCollection: SectionLookupRep<T, InflatingFunction<*>> = SectionLookupRep()

    /**
     *  A list of all attached recycler views
     */
    private val listeningRecyclers = mutableSetOf<WeakReference<RecyclerView>>()

    /**
     * Our own layoutinflater
     */
    private val inflater: LayoutInflater by lazy {
        LayoutInflater.from(context)
    }

    /**
     * The number of sections in this adapter
     */
    val sectionCount: Int
        get() = dataCollection.sectionCount


    /**
     * Delegates this responsibility to the data, since it knows it.
     * @param parent ViewGroup
     * @param viewType Int
     * @return BaseViewHolderItem<*>
     */
    override fun onCreateViewHolder(parent: ViewGroup, @IntRange(from = 0) viewType: Int): BaseViewHolderItem<*> {
        val rep = dataCollection.getTypeRepresentativeFromTypeValue(viewType)
        return rep?.invoke(inflater, parent, false)
                ?: throw RuntimeException("could not find item, " +
                        "even though we expected it, for viewType: $viewType;" +
                        "rep is = $rep;" +
                        "ViewGroup is = $parent")
    }

    /**
     * Delegates this responsibility to the data, since it knows it.
     * @param position Int
     * @return Int
     */
    override fun getItemViewType(@IntRange(from = 0) position: Int): Int {
        val index = dataCollection.indexToPath(position)
                ?: throw RuntimeException("Could not get index, so the item is not there;" +
                        " position = $position; count = ${dataCollection.size}")
        val result = dataCollection[index] ?: throw RuntimeException("element was null at index")
        return result.getTypeValue()
    }

    /**
     * Delegates this responsibility to the data, since it knows it.
     * and asks it to bind to the given view holder.
     * @param holder BaseViewHolderItem<*>
     * @param position Int
     */
    override fun onBindViewHolder(holder: BaseViewHolderItem<*>, @IntRange(from = 0) position: Int) {
        //lookup type to converter, then apply model on view using converter
        val index = dataCollection.indexToPath(position) ?: return
        val render = dataCollection[index]
        render?.bindToViewHolder(holder)
    }

    /**
     * Retrieves the number of items (total) in this adapter
     * @return Int the number of items (total) in this adapter
     */
    override fun getItemCount(): Int = dataCollection.size

    /**
     * Adds the given item to the end of the given section, or creates the section if not there
     * @param newItem T the item to add/append
     * @param inSection Int the section index (sparse) to add to
     */
    open fun add(newItem: T, inSection: Int): Unit = updateData {
        dataCollection.add(newItem, inSection)?.rawRow?.apply {
            notifyItemInserted(this)
        }
    }

    /**
     * Adds all the given items to the end of the given section, or creates the section if not there
     * @param items Collection<T> the items to add
     * @param inSection Int the section index (sparse) to add to
     */
    open fun addAll(items: Collection<T>, inSection: Int): Unit = updateData {
        dataCollection.addAll(items, inSection)?.inRaw?.apply {
            notifyItemRangeInserted(this.start, this.length)
        }

    }

    /**
     * Adds all the given items to the end of the given section, or creates the section if not there
     * @param items Array<out T> the items to add
     * @param inSection Int the section index (sparse) to add to
     */
    open fun addAll(vararg items: T, inSection: Int): Unit = updateData {
        dataCollection.addAll(items.asList(), inSection)?.inRaw?.apply {
            notifyItemRangeInserted(start, length)
        }
    }


    /**
     * Inserts (instead of appending /adding) to the given section, or creates the section if not there.
     * @param item T the item to insert
     * @param atRow Int what row to insert at (if there are data)
     * @param inSection Int the section index (sparse) to insert into
     */
    open fun insert(item: T, atRow: Int, inSection: Int): Unit = updateData {
        dataCollection.insert(item, atRow, inSection)?.rawRow?.apply {
            notifyItemInserted(this)
        }
    }

    /**
     * Inserts all the given elements at the given start position into the given section, or creates the section if not there.
     * @param items Collection<T> the items to insert at the given position
     * @param startPosition Int the place to perform the insert
     * @param inSection Int the section index (sparse) to insert into
     */
    open fun insertAll(items: Collection<T>, startPosition: Int, inSection: Int): Unit = updateData {
        dataCollection.insertAll(items, inSection, startPosition)?.inRaw?.apply {
            notifyItemRangeInserted(start, length)
        }
    }

    /**
     * Inserts all the given elements at the given start position into the given section, or creates the section if not there.
     * @param items Array<out T> the items to insert at the given position
     * @param startPosition Int the place to perform the insert
     * @param inSection Int the section index (sparse) to insert into
     */
    open fun insertAll(vararg items: T, startPosition: Int, inSection: Int): Unit = updateData {
        dataCollection.insertAll(items.asList(), startPosition, inSection)?.inRaw?.apply {
            notifyItemRangeInserted(start, length)
        }
    }

    /**
     * Removes the item in the section iff there
     * @param newItem T the object to remove
     * @param inSection Int the section index (sparse) to remove the object from
     * @return Int? the raw index of the removing iff any removing was performed (if not then null is returned)
     */
    open fun remove(newItem: T, inSection: Int): Int? = updateData {
        return@updateData dataCollection.removeItem(newItem, inSection)?.apply {
            notifyItemRemoved(rawRow)
        }?.rawRow
    }

    /**
     * Removes a given row inside of a section
     * Nothing happens if the row is not there.
     *
     * @param row Int the row in the section to remove
     * @param inSection Int the section to remove from
     */
    open fun removeAt(row: Int, inSection: Int): Unit = updateData {
        dataCollection.removeAt(row, inSection)?.rawRow?.apply {
            notifyItemRemoved(this)
        }
    }

    /**
     * Removes all the presented items from the given list of items
     * @param items List<T> the items to remove from the given section
     * @param inSection Int the section index (sparse) to remove all the items from (those that are there)
     */
    @UiThread
    open fun removeAll(items: List<T>, inSection: Int): Unit = updateData {
        items.forEach {
            remove(it, inSection)
        }
    }

    /**
     * Removes all the given elements from the given section iff possible
     * @param range kotlin.ranges.IntRange the range to remove from the section (rows)
     * @param inSection Int the section index (sparse) to remove the elements from
     */
    @UiThread
    open fun removeIn(range: kotlin.ranges.IntRange, inSection: Int): Unit = updateData {
        dataCollection.removeInRange(range, inSection)?.inRaw?.apply {
            notifyItemRangeRemoved(start + range.start, range.length)
        }
    }

    /**
     * Gets the item at the given row in the given section iff there.
     * @param atRow Int the row in the section to get
     * @param inSection Int the section index (sparse) to retrieve the row from
     * @return T? the potential item; if not there null is returned
     */
    @AnyThread
    open fun getItem(atRow: Int, inSection: Int): T? = dataCollection[atRow, inSection]

    /**
     * Clears all content of this adapter
     */
    @UiThread
    open fun clear(): Unit = updateData {
        dataCollection.clear()
        notifyDataSetChanged()
    }


    /**
     * Adds the given item to the given section without calling update on the underlying adapter
     * @param item T the item to append / add
     * @param inSection Int the section to append to
     */
    @UiThread
    protected fun addNoNotify(item: T, inSection: Int): Unit = updateData {
        dataCollection.add(item, inSection)?.rawRow
    }

    /**
     * Adds the given items to the given section without calling update on the underlying adapter
     * @param items List<T> the items to append / add
     * @param inSection Int the section to append to
     */
    @UiThread
    protected fun addNoNotify(items: List<T>, inSection: Int): Unit = updateData {
        dataCollection.addAll(items, inSection)?.inRaw
    }

    /**
     * Updates the given section with the given items.
     * @param items List<T> the items to set the section's content to
     * @param inSection Int the section index (sparse) to update
     */
    @UiThread
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

    /**
     * Sets the content of the given section to the given item
     * @param item T the item to set as the only content of the given section
     * @param inSection Int the section index (Sparse) to change
     */
    @UiThread
    fun setSection(item: T, inSection: Int) = setSection(listOf(item), inSection)

    /**
     * Clears the given section (removes all elements)
     * calling this on an empty section  / none exising section will have no effect
     * @param inSection Int the section to clear / remove
     */
    @UiThread
    fun clearSection(inSection: Int): Unit = updateData {
        dataCollection.clearSection(inSection)?.inRaw.apply {
            this?.let { notifyItemRangeRemoved(it.start, it.length) }
        }
    }

    /**
     * replaces the item at the position in the given section, with the supplied item
     * @param newItem T the new item to be inserted (iff possible)
     * @param position Int the position in the section to replace
     * @param inSection Int the section index (sparse) to change
     */
    @UiThread
    open fun replace(newItem: T, position: Int, inSection: Int): Unit = updateData {
        dataCollection.replace(newItem, position, inSection)?.rawRow.apply {
            this?.let { notifyItemChanged(it) }
        }
    }

    /**
     *Clears and sets a given section (sparse index) without notifying the underlying adapter
     * @param items List<T> the items to overwrite the given section with
     * @param inSection Int the seciton to clear and set
     * @param isIgnored Boolean if the section should be ignored
     * @return Boolean if it is ignored or not. (true if ignored)
     */
    @UiThread
    protected fun clearAndSetItemsNoNotify(items: List<T>, inSection: Int, isIgnored: Boolean) = updateData {
        dataCollection.setSection(items, inSection)
        isIgnored.ifTrue { dataCollection.ignoreSection(inSection) }
    }

    /**
     * Sets all sections
     * @param sections List<TypeSection<T>>
     */
    @UiThread
    protected fun setAllSections(sections: List<TypeSection<T>>) = updateData {
        dataCollection.setAllSections(sections)
        super.notifyDataSetChanged()
    }

    /**
     * Stops scroll for all RecyclerViews
     */
    @UiThread
    private fun stopScroll() {
        listeningRecyclers.forEach { recyclerView ->
            recyclerView.use { stopScroll() }
        }
    }

    /**
     * Called when we get bound to a recyclerView
     * @param recyclerView RecyclerView
     */
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        listeningRecyclers.add(recyclerView.weakReference())
    }

    /**
     * Called when a recyclerView is not going to use this adapter anymore.
     * @param recyclerView RecyclerView
     */
    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        listeningRecyclers.removeAll {
            it.get().isNullOrEqualTo(recyclerView)
        }
    }


    /**
     * Must be called from all things that manipulate the dataCollection.
     *
     * @param action EmptyFunctionResult<T> the action to execute safely
     * @return T the result of the action
     */
    @UiThread
    private inline fun <T> updateData(crossinline action: EmptyFunctionResult<T>): T {
        stopScroll()
        return action()
    }

    /**
     * Gets a representor of the given type (someone who can create the view)
     * @param viewHolderItem BaseViewHolderItem<*> the view holder to create it from)
     * @return InflatingFunction<*>? the potential inflater function iff anyone can create the view holders view type.
     * null if none can
     */
    @UiThread
    open fun getRepresentUsingType(viewHolderItem: BaseViewHolderItem<*>): InflatingFunction<*>? =
            dataCollection.getTypeRepresentativeFromTypeValue(viewHolderItem.viewBindingTypeValue)

    /**
     * Tries to lookup an item from a given raw index (0 until the item count)
     * @param rawIndex Int the raw index
     * @return T? the item if there, null otherwise
     */
    @UiThread
    open fun getItemFromRawIndex(rawIndex: Int): T? {
        val index = dataCollection.indexToPath(rawIndex) ?: return null
        return dataCollection[index]
    }

    /**
     * Hides the given section
     * only updates anything if the section was visible beforehand
     * @param sectionIndex Int the section index (sparse) to hide
     */
    @UiThread
    open fun hideSection(sectionIndex: Int) = updateData {
        val sectionLocation = dataCollection.ignoreSection(sectionIndex)?.inRaw ?: return@updateData
        notifyItemRangeRemoved(sectionLocation.start, sectionLocation.length)
    }

    /**
     * Shows the given section iff it was invisible before.
     * otherwise this have no effect
     * @param sectionIndex Int the section index (sparse) to show
     */
    @UiThread
    open fun showSection(sectionIndex: Int) = updateData {
        val sectionLocation = dataCollection.acceptSection(sectionIndex)?.inRaw ?: return@updateData
        notifyItemRangeInserted(sectionLocation.start, sectionLocation.length)
    }

    /**
     * Toggles all of the given sections visibility
     * @param sectionIndexes IntArray the section indexes (sparse indexes)
     */
    @UiThread
    open fun toggleSectionsVisibility(vararg sectionIndexes: Int) {
        sectionIndexes.forEach(this::toggleSectionVisibility)
    }

    /**
     * Toggles the given sections visibility
     * so if it was visible it becomes invisible, and vice verca
     * @param sectionIndex Int the section index (sparse) to update
     */
    @UiThread
    open fun toggleSectionVisibility(sectionIndex: Int) {
        val sectionData = dataCollection.sectionAt(sectionIndex) ?: return
        if (sectionData.isIgnored) {
            showSection(sectionIndex)
        } else {
            hideSection(sectionIndex)
        }
    }

    /**
     * Clears the underlying data without notifying the underlying adapter
     */
    @AnyThread
    protected fun clearNoNotify() {
        dataCollection.clear()
    }

    /**
     * Tries to find the given element in a section , and returns the path if found.
     * @param item T the item to find (must be compareable to be compared, otherwise it will be per address)
     * @param inSection Int the section index(sparse)
     * @return IndexPath? the index where the element is, if found, null otherwise (or null also if the section is not there)
     */
    @UiThread
    fun getIndexFor(item: T, @IntRange(from = 0) inSection: Int): IndexPath? {
        val innerIndex = dataCollection.sectionAt(inSection)?.collection?.indexOf(item)
                ?: return null
        return IndexPath(innerIndex, inSection)
    }

    /**
     * Retrieves the given section's size (number of elements)
     * @param sectionIndex Int the section index ( sparse) to query
     * @return Int? the number of item in this section (elements) , or null if the section does not exists
     */
    @UiThread
    fun getSectionSize(sectionIndex: Int): Int? = dataCollection.sectionAt(sectionIndex)?.size

    /**
     * Tells if the queried section is visible; defaults to false if the section does not exists
     * @param inSection Int the section index (sparse) to query
     * @return Boolean true if the section is visible, false otherwise
     */
    @UiThread
    fun isSectionVisible(inSection: Int): Boolean =
            dataCollection.sectionAt(inSection)?.isIgnored?.not() ?: false


    /**
     * Changes the given section's visibility to the given visibility
     * @param section Int the section index (sparse) to update
     * @param isVisible Boolean if true then its visible, if false then its invisible.
     */
    @UiThread
    fun setSectionVisibility(section: Int, isVisible: Boolean) {
        dataCollection.sectionAt(section)?.let {
            if (it.isIgnored != !isVisible) {
                toggleSectionVisibility(section)
            }
        }
    }


    /**
     * Removes the section by index, iff it exists.
     * @param sectionIndex Int the section index (sparse) to remove.
     */
    @UiThread
    open fun removeSection(@IntRange(from = 0) sectionIndex: Int) = clearSection(sectionIndex)

    /**
     * Removes a given list of sections
     *
     * @param sectionIndexes IntArray the sections (sparse) to remove
     */
    @UiThread
    fun removeSections(@IntRange(from = 0) vararg sectionIndexes: Int) {
        sectionIndexes.forEach(this::removeSection)
    }


    /**
     * Smooth scrolls to the given section start
     * @param sectionIndex Int the section index (sparse)
     */
    @UiThread
    fun smoothScrollToSection(@IntRange(from = 0) sectionIndex: Int) {
        val positionInList = dataCollection.getSectionLocation(sectionIndex)?.inRaw?.first ?: return
        listeningRecyclers.forEach {
            it.use { this.smoothScrollToPosition(positionInList) }
        }
    }

    /**
     * Reloads all items in this adapter
     */
    @UiThread
    fun reloadAll() {
        notifyItemRangeChanged(0, dataCollection.size)
    }

    /**
     * Reloads a given section's items
     * @param sectionIndex Int the section index (sparse) to reload
     */
    @UiThread
    fun reloadSection(sectionIndex: Int) {
        val location = dataCollection.getSectionLocation(sectionIndex) ?: return
        notifyItemRangeChanged(location.inRaw)
    }


}

/**
 * Hides all the given sections (by sparse index)
 * calling hide on an already invisible section have no effects
 * @receiver BaseDataBindingRecyclerAdapter
 * @param sections IntArray the sections to hide
 */
@UiThread
fun BaseDataBindingRecyclerAdapter.hideSections(vararg sections: Int) =
        sections.forEach(this::hideSection)

/**
 * Shows all the given sections (by sparse index)
 * calling show on an already visible section have no effects
 * @receiver BaseDataBindingRecyclerAdapter
 * @param sections IntArray the sections to show
 */
fun BaseDataBindingRecyclerAdapter.showSections(vararg sections: Int) =
        sections.forEach(this::showSection)

open class BaseDataBindingRecyclerAdapter(context: Context) :
        DataBindingRecyclerAdapter<IRenderModelItem<*, *>>(context)


class DefaultDataBindingRecyclerAdapter(context: Context) : BaseDataBindingRecyclerAdapter(context)