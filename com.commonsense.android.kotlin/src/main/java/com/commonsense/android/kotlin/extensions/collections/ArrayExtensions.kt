package com.commonsense.android.kotlin.extensions.collections

/**
 * Created by Kasper Tvede on 11-07-2017.
 */


fun IntProgression.toIntArray(): IntArray {
    return this.toList().toIntArray()
}