package com.commonsense.android.kotlin.base.extensions

import android.text.Editable
import kotlin.system.measureNanoTime

/**
 * Created by Kasper Tvede on 05-06-2017.
 */
fun measureSecondTime(function: () -> Unit): Long {
    val time = measureNanoTime(function)
    return time / 10_00_000_00L //nano = 100 millionth of a second
}

/**
 * converts an immutable string to an editable edition :)
 */
fun String.toEditable(): Editable {
    return Editable.Factory.getInstance().newEditable(this)
}

val Any?.isNull
    get() = this == null

val Any?.isNotNull
    get() = this != null


fun <T> T?.isNullOrEqualTo(other: T?): Boolean = this == null || this == other
