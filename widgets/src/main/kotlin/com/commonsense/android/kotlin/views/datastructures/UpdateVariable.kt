package com.commonsense.android.kotlin.views.datastructures

import com.commonsense.android.kotlin.base.EmptyFunction
import com.commonsense.android.kotlin.base.extensions.collections.onTrue

/**
 * Created by Kasper Tvede on 13-06-2017.
 */

/**
 * Represents an updateable variable for use in a custom view or custom view component
 * @param T the type of the variable
 * @property onUpdated Function0<Unit> the function to call when the value changes
 * @property innerVariable T the real value
 * @property value T the accessor for the inner variable, also calling the onUpdated callback
 */
class UpdateVariable<T>(initialValue: T,
                        val onUpdated: EmptyFunction) {
    /**
     * the real hidden variable
     */
    private var innerVariable = initialValue

    /**
     * The value
     */
    var value: T
        get() = innerVariable
        set(value) {
            val didChange = (innerVariable != value)
            innerVariable = value
            didChange.onTrue(onUpdated)
        }

    /**
     * Does not call the onUpdated callback
     *
     * @param value T the new value to set the inner variable to
     */
    fun setWithNoUpdate(value: T) {
        innerVariable = value
    }
}