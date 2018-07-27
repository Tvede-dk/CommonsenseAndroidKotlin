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
class ToggleSelectionViewHandler<T>(callback: FunctionUnit<Set<T>>)
    : BaseSelectionHandler<T, Set<T>, MutableSet<T>>(callback) {

    override var selection: MutableSet<T> = mutableSetOf()

    override fun handleSelectionChanged(view: ToggleableView<T>, selectedValue: Boolean) {
        selection.setExistence(view.value, selectedValue)
        callback.invoke(selection)
    }

    override fun isSelected(view: ToggleableView<T>): Boolean {
        return selection.contains(view.value)
    }

    override fun removeSelected() {
        selection.clear()
    }


//    fun addView(view: ToggleableView<T>, preSelect: Boolean = false) {
//        view.setOnSelectionChanged(this::onSelectionChanged)
//        viewsToWorkOn.add(view)
//        view.checked = preSelect
//    }

    /**
     * Select the given values
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