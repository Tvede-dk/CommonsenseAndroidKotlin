package com.commonsense.android.kotlin.base.extensions

/**
 * Created by Kasper Tvede on 26-08-2017.
 */
fun Float.equals(otherFloat: Float, delta: Float): Boolean
        = this >= otherFloat - delta && this <= otherFloat + delta
