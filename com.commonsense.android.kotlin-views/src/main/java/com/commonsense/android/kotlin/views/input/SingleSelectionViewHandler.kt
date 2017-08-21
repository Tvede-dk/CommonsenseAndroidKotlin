package com.commonsense.android.kotlin.views.input

/**
 * Created by kasper on 21/08/2017.
 */
class SingleSelectionViewHandler<T> {
    // use toggleable view
    private val viewsToWorkOn = mutableListOf<ToggleableView<T>>()

    private var lastSelected: ToggleableView<T>? = null
    private var selected: ToggleableView<T>? = null

    fun addView(view: ToggleableView<T>) {
        view.setOnSelectionChanged { onSelectionChanged(view, it) }
    }

    private var isAllowedToUnselectedAll: Boolean = false

    private fun onSelectionChanged(view: ToggleableView<T>, it: Boolean) {
        if (it) {
            //we have changed selection, update old, and move "down" the chain.
            selected?.isSelected = false
            lastSelected = selected
            selected = view
        } else {
            //by default it will be unselected, however if that is not allowed,
            // then we are to correct that "mistake"
            if (!isAllowedToUnselectedAll) {
                view.isSelected = true
            }
        }

    }

}