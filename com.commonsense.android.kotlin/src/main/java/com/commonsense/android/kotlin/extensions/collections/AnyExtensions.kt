package com.commonsense.android.kotlin.extensions.collections

/**
 * Created by Kasper Tvede on 06-12-2016.
 */
inline fun <T> Any.ifElse(performIf: Boolean, crossinline onTrue: () -> T?,crossinline  onFalse: () -> T?): T? {
    return if (performIf) {
        onTrue()
    } else {
        onFalse()
    }
}
