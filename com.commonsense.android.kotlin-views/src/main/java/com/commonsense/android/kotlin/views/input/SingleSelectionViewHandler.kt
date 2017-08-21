package com.commonsense.android.kotlin.views.input

/**
 * Created by kasper on 21/08/2017.
 */
class SingleSelectionViewHandler<T> {
    // use toggleable view
    var viewsToWorkOn = mutableListOf<ToggleableView<T>>()

    private var lastSelected: ToggleableView<T>? = null
    private var selected: ToggleableView<T>? = null


}