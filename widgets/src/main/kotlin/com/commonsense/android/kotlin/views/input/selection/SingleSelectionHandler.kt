package com.commonsense.android.kotlin.views.input.selection

import com.commonsense.android.kotlin.base.EmptyFunction
import com.commonsense.android.kotlin.base.FunctionUnit
import com.commonsense.android.kotlin.base.extensions.parseTo

/**
 *  Handles a group of ToggleableViews , such that you can select at max 1 of them
 *  and any other selection will change that selection.
 *  This is very similar to the concept of a Radiobutton group,
 *  except we provide a value on each selection in the callback,
 *  thus removing all kinds of worries about which view is selected and
 *  extracting / calculating a value from that
 *  it can also be configured to allow deselection, by calling the  allowDeselection.
 */
class SingleSelectionHandler<T>(callback: FunctionUnit<T>) :
        BaseSelectionHandler<T, T, ToggleableView<T>?>(callback) {


    /**
     * The one we should interact with, it handles the set-ting correctly.
     */
    override var selection: ToggleableView<T>? = null
        set(value) {
            //deselect previous one if any
            field?.deselect()
            //set newly selected (if any) and at the same time call the callback notifying about any
            // selection changes.
            field = value.parseTo(this::toggleCallbackToCallback)
        }

//    /**
//     * The views we are working on, akk the once that can be selected and deselected (only 1 selected at a time).
//     *
//     * Consideration: should this be a Set, but will that cause any issues ?
//     *
//     *
//     */
//    private val viewsToWorkOn = mutableListOf<ToggleableView<T>>()

//    /**
//     *Simple guard against callback hell / massacre
//     */
//    private var innerDisableSelectionChanged = false

    /**
     * The one we should interact with, it handles the setting correctly.
     */
//    private var selected: ToggleableView<T>? = null
//        set(value) {
//            //deselect previous one if any
//            field?.deselect()
//            //set newly selected (if any) and at the same time call the callback notifying about any
//            // selection changes.
//            field = value.parseTo(this::toggleCallbackToCallback)
//        }

    /**
     * Notifies the callback about a selection change, if any callback are registered.
     */
    private fun toggleCallbackToCallback(toggleView: ToggleableView<T>) =
            callback.invoke(toggleView.value)

    /**
     * Whenever we are allowed to de / un select all (the user)
     * this can / will happen on start; and if the last selected view gets removed.
     */
    private var isAllowedToUnselectedAll: Boolean = false

    /**
     *  A callback that gets called every time we deselect an item (so no selection exists)
     *  this allows you to deselect all views.
     *  this is mostly a special case, as the regular callback does not allow "null" as a value,
     *  but this is actually that case.
     */
    fun allowDeselection(onDeselection: EmptyFunction) {
        onDeselectCallback = onDeselection
        isAllowedToUnselectedAll = true
    }

    /**
     * Holding the on deselection callback if any.
     */
    private var onDeselectCallback: EmptyFunction? = null


//    /**
//     * Controls iff we allow the user to un-select the current selected.
//     *
//     */
//    private fun onSelectionChanged(view: ToggleableView<T>, selection: Boolean) = updateSelection {
//        when {
//            selection && selected != view -> selected = view
//            //if not allowed to deselect, make the "deselected" view selected again :)
//            selected == view && !selection && !isAllowedToUnselectedAll -> selected?.select()
//            //if allowed, then nothing is selected.
//            selected == view && !selection && isAllowedToUnselectedAll -> {
//                selected = null
//                onDeselectCallback?.invoke()
//            }
//        }
//    }


    /**
     * Changes the selection to the given value;
     * so if the value is 42, and the 3 view in this contains that value, then that view is selected
     * however it requires the value to be equatable. either though reference (pointer)
     * or by implementing equal.
     * It will run though all views,
     * so if there are multiple views with the same value (which is an error) it will select the last one.
     *
     * This is O(n) where n is the number of views.
     */
    fun setSelectedValue(selectedValue: T?) = updateSelection {
        //find the view with the value and select it.
        viewsToWorkOn.forEach {
            it.checked = it.value == selectedValue
            if (it.checked) {
                selection = it
            }
        }

    }

//    /**
//     * Updates the internal selection of this view, via a guard, to avoid any kind of callback hell.
//     * It performs the action inside of the "guard"
//     * (where changing selection will not cause an infinite loop effect).
//     */
//    private inline fun updateSelection(crossinline action: EmptyFunction) {
//        if (innerDisableSelectionChanged) {
//            return
//        }
//        innerDisableSelectionChanged = true
//        action()
//        innerDisableSelectionChanged = false
//    }


//    /**
//     * Adds the given toggleable view to the list of views that we can select between.
//     */
//    operator fun plusAssign(selectionToAdd: ToggleableView<T>) {
//        addView(selectionToAdd)
//    }
//
//    /**
//     * De attaches the given view from the views we are working on
//     */
//    operator fun minusAssign(selectionToRemove: ToggleableView<T>) {
//        removeView(selectionToRemove)
//    }
//
//    /**
//     * De attaches the given view from the views we are working on
//     */
//    fun removeView(view: ToggleableView<T>) = updateSelection {
//        view.deselect()
//        view.clearOnSelectionChanged()
//        if (selected == view) {
//            selected = null
//        }
//        viewsToWorkOn.remove(view)
//    }
//
//    /**
//     * Adds the given toggleable view to the list of views that we can select between.
//     */
//    fun addView(view: ToggleableView<T>) = updateSelection {
//        view.deselect()
//        view.setOnSelectionChanged(this::onSelectionChanged)
//        viewsToWorkOn.add(view)
//    }

    override fun handleSelectionChanged(view: ToggleableView<T>, selectedValue: Boolean) {
        when {
            selectedValue && selection != view -> selection = view
            //if not allowed to deselect, make the "deselected" view selected again :)
            selection == view && !selectedValue && !isAllowedToUnselectedAll -> selection?.select()
            //if allowed, then nothing is selected.
            selection == view && !selectedValue && isAllowedToUnselectedAll -> {
                selection = null
                onDeselectCallback?.invoke()
            }
        }
    }

    override fun isSelected(view: ToggleableView<T>): Boolean = selection == view

    override fun removeSelected() {
        selection = null
    }

}