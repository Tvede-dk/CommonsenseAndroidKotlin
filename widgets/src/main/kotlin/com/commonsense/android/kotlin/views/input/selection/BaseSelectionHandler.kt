package com.commonsense.android.kotlin.views.input.selection

import com.commonsense.android.kotlin.base.*

/**
 *
 */
abstract class BaseSelectionHandler<T, CallbackType, SelectionType>(
        val callback: FunctionUnit<CallbackType>) {

    protected abstract var selection: SelectionType

    /**
     *Simple guard against callback hell / massacre
     */
    private var innerDisableSelectionChanged = false


    /**
     * The views we are working on, akk the once that can be selected and deselected (only 1 selected at a time).
     *
     * Consideration: should this be a Set, but will that cause any issues ?
     *
     *
     */
    protected val viewsToWorkOn = mutableSetOf<ToggleableView<T>>()


    /**
     * Updates the internal selection of this view, via a guard, to avoid any kind of callback hell.
     * It performs the action inside of the "guard"
     * (where changing selection will not cause an infinite loop effect).
     */
    protected fun updateSelection(action: EmptyFunction) {
        if (innerDisableSelectionChanged) {
            return
        }
        innerDisableSelectionChanged = true

        action()
        innerDisableSelectionChanged = false
    }


    /**
     * Adds the given toggleable view to the list of views that we can select between.
     */
    operator fun plusAssign(selectionToAdd: ToggleableView<T>) {
        addView(selectionToAdd)
    }

    /**
     * De attaches the given view from the views we are working on
     */
    operator fun minusAssign(selectionToRemove: ToggleableView<T>) {
        removeView(selectionToRemove)
    }

    /**
     * De attaches the given view from the views we are working on
     */
    fun removeView(view: ToggleableView<T>) = updateSelection {
        view.deselect()
        view.clearOnSelectionChanged()
        if (isSelected(view)) {
            removeSelected()
        }
        viewsToWorkOn.remove(view)
    }

    /**
     * Adds the given toggleable view to the list of views that we can select between.
     */
    //TODO  preSelect: Boolean = false ?
    fun addView(view: ToggleableView<T>) = updateSelection {
        view.deselect()
        view.setOnSelectionChanged(this::onSelectionChanged)
        viewsToWorkOn.add(view)
    }

    protected fun onSelectionChanged(view: ToggleableView<T>, selection: Boolean) = updateSelection {
        handleSelectionChanged(view, selection)
    }

    abstract fun handleSelectionChanged(view: ToggleableView<T>, selectedValue: Boolean)

    protected abstract fun isSelected(view: ToggleableView<T>): Boolean

    protected abstract fun removeSelected()
}