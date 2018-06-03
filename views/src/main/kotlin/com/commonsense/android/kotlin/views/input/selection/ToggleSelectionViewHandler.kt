package com.commonsense.android.kotlin.views.input.selection

import com.commonsense.android.kotlin.base.FunctionUnit
import com.commonsense.android.kotlin.base.extensions.collections.setExistence

/**
 * Created by kasper on 24/08/2017.
 */

/**
 * A toggle selection (many) view handler
 * only assumption; we only have a set of values, not multiple of each.
 */
class ToggleSelectionViewHandler<T> {

    private val selectedValues = mutableSetOf<T>()
    private val viewsToWorkOn = mutableSetOf<ToggleableView<T>>()


    var callback: FunctionUnit<Set<T>>? = null

    fun addView(view: ToggleableView<T>, preSelect: Boolean = false) {
        view.setOnSelectionChanged(this::onSelectionChanged)
        viewsToWorkOn.add(view)
        view.checked = preSelect
    }

    private fun onSelectionChanged(view: ToggleableView<T>, selection: Boolean) {
        selectedValues.setExistence(view.value, selection)
        callback?.invoke(selectedValues)
    }

    operator fun plusAssign(selectionToAdd: ToggleableView<T>) {
        addView(selectionToAdd)
    }

    /**
     *
     * Performance : assumes the set has "contains" as O(1) otherwise this will be a O(n^2) algorithm
     */
    fun selectValues(values: Set<T>) {
        viewsToWorkOn.forEach {
            if (it.value in values) {
                it.select()
            }
        }
    }

}