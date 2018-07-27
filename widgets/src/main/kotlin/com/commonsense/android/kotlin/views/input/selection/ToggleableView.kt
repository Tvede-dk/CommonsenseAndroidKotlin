package com.commonsense.android.kotlin.views.input.selection

import android.view.View
import android.widget.*
import com.commonsense.android.kotlin.base.*


/**
 * The selection toggle callback type
 */
typealias SelectionToggleCallback<T> = (ToggleableView<T>, Boolean) -> Unit

/**
 * the selection change callback type
 */
typealias SelectionChangeCallback<ViewType> = (ViewType, EmptyFunction) -> Unit

/**
 * Required for marking things as "checked"
 */
interface CheckableStatus {
    var checked: Boolean
}

/**
 * Both containing a checked status but also the ability to set / clear a callback on checkChanged
 */
interface CheckableStatusCallback : CheckableStatus {
    fun setOnCheckedChangedListener(callback: EmptyFunction)
    fun clearOnSelectionChanged()
}

/**
 * The ability to select and "deselect" a view, with a given value.
 */
interface ToggleableView<out T> : CheckableStatus {
    /**
     * The value this "view" represents (the checked state).
     */
    val value: T

    /**
     *  calls the given callback when the selection changes.
     */
    fun setOnSelectionChanged(callback: SelectionToggleCallback<T>)


    /**
     * Removes any selection callbacks
     */
    fun clearOnSelectionChanged()

    /**
     * Deselects the checked state
     */
    fun deselect() {
        checked = false
    }

    /**
     * Selects the checked state
     */
    fun select() {
        checked = true
    }

}

/**
 * A functional wrapper over the ToggleableView, where the implementation is provided though functions.
 */
class ToggleableViewFunctional<out T, ViewType : View>(
        override val value: T,
        private val view: ViewType,
        private val getSelection: Function1<ViewType, Boolean>,
        private val setSelection: Function2<ViewType, Boolean, Unit>,
        private val setOnChangedListener: SelectionChangeCallback<ViewType>,
        private val clearOnChangedListener: FunctionUnit<ViewType>) : ToggleableView<T> {

    override fun clearOnSelectionChanged() {
        this.clearOnChangedListener(view)
    }

    override var checked: Boolean
        get() = getSelection(view)
        set(value) = setSelection(view, value)

    override fun setOnSelectionChanged(callback: SelectionToggleCallback<T>) {
        this.setOnChangedListener(view) {
            callback(this, checked)
        }
    }
}

/**
 * Wraps a view that is also a checkableStatus into a toggleable.
 * Given the functional implementations.
 */
fun <T, ViewType> ViewType.asToggleable(
        value: T,
        setOnChangedListener: SelectionChangeCallback<ViewType>,
        clearOnChangedListener: FunctionUnit<ViewType>)
        : ToggleableViewFunctional<T, ViewType> where ViewType : View,
                                                      ViewType : CheckableStatus {

    return ToggleableViewFunctional(
            value,
            this,
            { it.checked },
            { view, isChecked ->
                view.checked = isChecked
            },
            setOnChangedListener,
            clearOnChangedListener)
}

/**
 * Wraps a view in a toggleable functional, thus making it possible to treat any view as a toggleable,
 * given the provided selection mechanisms
 */
fun <T, ViewType : View> ViewType.asToggleable(
        value: T,
        getSelection: (ViewType) -> Boolean,
        setSelection: (ViewType, Boolean) -> Unit,
        setOnChangedListener: SelectionChangeCallback<ViewType>,
        clearOnChangedListener: FunctionUnit<ViewType>): ToggleableView<T> {

    return ToggleableViewFunctional(
            value,
            this,
            getSelection,
            setSelection,
            setOnChangedListener,
            clearOnChangedListener)
}


/**
 * Creates a functional wrapper over the given view, with the given value, as toggleable.
 * the viewType is capable of setting selection and clearing it.
 */
fun <T, ViewType> ViewType.asToggleable(value: T): ToggleableViewFunctional<T, ViewType>
        where ViewType : View,
              ViewType : CheckableStatusCallback {
    return this.asToggleable(value,
            CheckableStatusCallback::setOnCheckedChangedListener,
            CheckableStatusCallback::clearOnSelectionChanged)
}

/**
 * Converts a CompoundButton to a toggleable view.
 * this then works for
 * CheckBox, RadioButton, Switch, ToggleButton
 * The value T is the selection value if this view is selected / marked.
 */
fun <T> CompoundButton.asToggleable(value: T): ToggleableView<T> {
    return this.asToggleable(value,
            android.widget.CompoundButton::isChecked,
            android.widget.CompoundButton::setChecked,
            { compoundButton, function ->
                compoundButton.setOnCheckedChangeListener { _, _ -> function() }
            },
            { compoundButton ->
                compoundButton.setOnCheckedChangeListener(null)
            })
}
