package com.commonsense.android.kotlin.views.input.selection

import android.view.View
import com.commonsense.android.kotlin.base.EmptyFunction

/**
 * Created by kasper on 22/08/2017.
 */
typealias SelectionToggleCallback<T> = (ToggleableView<T>, Boolean) -> Unit

typealias SelectionChangeCallback<ViewType> = (ViewType, EmptyFunction) -> Unit

interface CheckableStatus {
    var checked: Boolean
}

interface CheckableStatusCallback : CheckableStatus {
    fun setOnCheckedChanged(callback: EmptyFunction)
}

interface ToggleableView<out T> : CheckableStatus {
    val value: T

    fun setOnSelectionChanged(callback: SelectionToggleCallback<T>)

    fun deselect(){
        checked = false
    }
    fun select(){
        checked = true
    }
}

class ToggleableViewFunctional<out T, ViewType : View>(
        override val value: T,
        private val view: ViewType,
        private val getSelection: (ViewType) -> Boolean,
        private val setSelection: (ViewType, Boolean) -> Unit,
        private val setOnChangedListener: SelectionChangeCallback<ViewType>) : ToggleableView<T> {

    override var checked: Boolean
        get() = getSelection(view)
        set(value) = setSelection(view, value)

    override fun setOnSelectionChanged(callback: SelectionToggleCallback<T>) {
        this.setOnChangedListener(view) {
            callback(this, checked)
        }
    }
}

fun <T, ViewType> ViewType.asToggleable(
        value: T, setOnChangedListener: SelectionChangeCallback<ViewType>)
        : ToggleableViewFunctional<T, ViewType>
        where ViewType : View, ViewType : CheckableStatus {


    return ToggleableViewFunctional(value,
            this,
            { it.checked },
            { view, isChecked -> view.checked = isChecked },
            setOnChangedListener)
}

fun <T, ViewType : View> ViewType.asToggleable(value: T,
                                               getSelection: (ViewType) -> Boolean,
                                               setSelection: (ViewType, Boolean) -> Unit,
                                               setOnChangedListener: SelectionChangeCallback<ViewType>)
        : ToggleableView<T> {
    return ToggleableViewFunctional(value, this, getSelection, setSelection, setOnChangedListener)
}


fun <T, ViewType> ViewType.asToggleable(value: T): ToggleableViewFunctional<T, ViewType>
        where ViewType : View, ViewType : CheckableStatusCallback {
    return this.asToggleable(value, CheckableStatusCallback::setOnCheckedChanged)
}