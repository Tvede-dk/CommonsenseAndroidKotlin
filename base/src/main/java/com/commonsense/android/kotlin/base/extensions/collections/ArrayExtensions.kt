package com.commonsense.android.kotlin.base.extensions.collections

/**
 * Created by Kasper Tvede on 11-07-2017.
 */


/**
 * maps a progression into an array of all the values.
 */
fun IntProgression.toIntArray(): IntArray = this.toList().toIntArray()

val IntProgression.length
    get() = ((last + step) - first) / step //+ step due to "inclusive".

