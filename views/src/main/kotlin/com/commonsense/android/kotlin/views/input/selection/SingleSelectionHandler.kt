package com.commonsense.android.kotlin.views.input.selection

import com.commonsense.android.kotlin.base.EmptyFunction
import com.commonsense.android.kotlin.base.FunctionUnit
import com.commonsense.android.kotlin.base.extensions.parseTo

/**
 * Created by kasper on 21/08/2017.
 */
class SingleSelectionHandler<T> {
    // use toggleable view
    private val viewsToWorkOn = mutableListOf<ToggleableView<T>>()


    //simple guard against callback hell / massacre
    private var innerDisableSelectionChanged = false

    private var _selected: ToggleableView<T>? = null
    private var selected: ToggleableView<T>?
        get() = _selected
        set(value) {
            _selected = value.parseTo(this::toggleCallbackToCallback)
        }

    private fun toggleCallbackToCallback(toggleView: ToggleableView<T>) {
        callback?.invoke(toggleView.value)
    }

    var callback: FunctionUnit<T>? = null

    private var isAllowedToUnselectedAll: Boolean = false

    fun allowDeselection(onDeselection: EmptyFunction) {
        onDeselectCallback = onDeselection
        isAllowedToUnselectedAll = true
    }

    private var onDeselectCallback: EmptyFunction? = null


    fun addView(view: ToggleableView<T>) = updateSelection {
        view.deselect()
        view.setOnSelectionChanged(this::onSelectionChanged)
        viewsToWorkOn.add(view)
    }

    /**
     * Controls iff we allow the user to un-select the current selected.
     */


    private fun onSelectionChanged(view: ToggleableView<T>, selection: Boolean) = updateSelection {
        when {
            selection && selected != view -> changeSelection(view)

        //if not allowed to deselect, make the "deselected" view selected again :)
            selected == view && !selection && !isAllowedToUnselectedAll -> selected?.select()
        //if allowed, then nothing is selected.
            selected == view && !selection && isAllowedToUnselectedAll -> {
                selected = null
                onDeselectCallback?.invoke()
            }
        }
    }

    private fun changeSelection(view: ToggleableView<T>) {
        selected?.deselect()
        selected = view
    }

    fun setSelectedValue(selectedValue: T?) = updateSelection {
        //find the view with the value and select it.
        viewsToWorkOn.forEach {
            it.checked = it.value == selectedValue
            if (it.checked) {
                selected = it
            }
        }

    }

    private inline fun updateSelection(crossinline action: EmptyFunction) {
        if (innerDisableSelectionChanged) {
            return
        }
        innerDisableSelectionChanged = true
        action()
        innerDisableSelectionChanged = false
    }


    operator fun plusAssign(selectionToAdd: ToggleableView<T>) {
        addView(selectionToAdd)
    }

}