package com.commonsense.android.kotlin.views.input

import com.commonsense.android.kotlin.base.FunctionUnit

/**
 * Created by kasper on 22/08/2017.
 */
interface ToggleableView<out T> {
    var isSelected: Boolean
    val value: T

    fun setOnSelectionChanged(callback: FunctionUnit<Boolean>)
}