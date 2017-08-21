package com.commonsense.android.kotlin.views.input

/**
 * Created by kasper on 22/08/2017.
 */
interface ToggleableView<out T> {
    var isSelected: Boolean
    val value: T
}