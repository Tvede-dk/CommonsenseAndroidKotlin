package com.commonsense.android.kotlin.collections

import onTrue

/**
 * Created by Kasper Tvede on 13-06-2017.
 */

class UpdateVariable<T>(initialValue: T, val onUpdated: () -> Unit) {
    private var innerVariable = initialValue
    var value: T
        get() = innerVariable
        set(value) {
            val didChange = (innerVariable != value)
            innerVariable = value
            didChange.onTrue(onUpdated)
        }

    fun setWithNoUpdate(value: T) {
        innerVariable = value
    }
}