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

    //simple guard against callback hell / massacre
    private var innerDisableSelectionChanged = false


    fun addView(view: ToggleableView<T>) = updateSelection {
        view.deselect()
        view.setOnSelectionChanged(this::onSelectionChanged)
        viewsToWorkOn.add(view)
    }

    private var isAllowedToUnselectedAll: Boolean = false

    private fun onSelectionChanged(view: ToggleableView<T>, selection: Boolean) = updateSelection {
        when {
            selection && selected != view -> {
                selected?.deselect()
                selected = view
                return@updateSelection
            }
            selected == view && !selection && !isAllowedToUnselectedAll -> selected?.select()
        //by default it will be unselected, however if that is not allowed,
        // then we are to correct that "mistake" (reselect it)
        //   !selection && !isAllowedToUnselectedAll -> selected?.select()
        }
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